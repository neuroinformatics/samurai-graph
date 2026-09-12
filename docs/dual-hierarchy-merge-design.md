# Dual Class Hierarchy Merge Design

## Context

Samurai Graph organizes figure rendering into two packages:

- `jp.riken.brain.ni.samuraigraph.figure` - shared model and drawing logic
- `jp.riken.brain.ni.samuraigraph.figure.java2d` - Java2D renderer classes

Each drawing element exists twice: an abstract model class in `figure`
(e.g. `SGDrawingElementLine`) and a renderer subclass in `figure.java2d`
(e.g. `SGDrawingElementLine2D`). This doubles the maintenance surface and
makes it hard to reason about an element's complete behavior.

## Current State

| Measure | Value |
|---------|-------|
| Classes in `figure` | 60 |
| Classes in `figure.java2d` | 128 |
| `figure.java2d` -> `figure` imports | 128 of 128 files |
| `figure` -> `figure.java2d` imports | 3 files (`SGElementGroup`, `SGLineStylePropertyDialog`, `SGXYFigure`) |
| Renderer classes with a model counterpart | 11 (`SGDrawingElement*2D` <- `SGDrawingElement*`) |
| Files outside `figure` importing `figure.java2d` | 10 (`application`/`data`/`base`) |
| Simple-name collisions between the two packages | none |

The dependency is essentially one-way (renderer depends on model), there
are no name collisions, and the external surface of `figure.java2d` is
small (10 files). This makes a merge tractable.

## Goals

- Merge `figure` and `figure.java2d` into a single source package.
- Collapse the 11 `SGDrawingElement*2D` renderer subclasses into their
  model counterparts so that each element has one class.
- Keep the public API of the merged classes source-compatible wherever
  possible; update the 10 external importers explicitly.
- No behavior change; the same binary behavior before and after each step.

## Non-Goals

- Naming cleanup of `*InGraph` / `*ForData` classes (deferred to a later
  cleanup pass).
- Merging the property dialog families beyond what is already shared.
- Changing the on-disk layout of the JAR (package identifiers are internal
  to the application).

## Design Decision

Move all 128 classes into `jp.riken.brain.ni.samuraigraph.figure` and fold
the `*2D` renderers into their model bases. The merge proceeds in
independent, individually verifiable phases because phases P1 (package
move) and P2 (class collapse) do not depend on each other.

The package move is safe because:

- No simple-name collisions exist between the two packages.
- The import direction is one-way, so `figure` classes need almost no
  changes (3 files need review).
- Only 10 external files import `figure.java2d`, so the import rewrite is
  bounded.

## Phases

### P0: Safety net (already in place)

- Legend, axis, and data setup dialog property round-trip / construction
  tests guard the most touched code paths.
- Full build (`./mvnw clean test`) is the gate for every phase.

### P1: Package unification

- Move all files from `figure/java2d/` to `figure/`.
- Rewrite `import jp.riken.brain.ni.samuraigraph.figure.java2d.*` to
  `import jp.riken.brain.ni.samuraigraph.figure.*` inside the moved classes
  (about 128 files) and in the 10 external files.
- Add no new API; the class names stay unchanged, so `application`/`data`/
  `base` code compiles after the import rewrite.
- Remove the empty `figure.java2d` package directory and any leftover
  qualified references.
- Gate: clean compile + full test suite.

### P2: Collapse `*2D` renderer subclasses

- For each of the 11 pairs, inline the `*2D` class into its model base:
  move the members and behavior into `SGDrawingElementX`, then delete
  `SGDrawingElementX2D` and update references.
- Some renderer bases are themselves abstract and instantiated by
  concrete java2d classes (e.g. `SGDrawingElementLine2DExtended`); merge
  them together with their model counterpart in the same step.
- Keep `equals`/`hashCode`/serialization behavior intact (check
  `SGProperties` round trips).
- Gate: clean compile + full test suite after each pair; add a small
  render smoke test where feasible (elements are Swing/AWT free for pure
  geometry methods).

### P3: Reference cleanup and documentation

- Remove `figure.java2d` notes from `PROBLEMS.md` and search for stale
  references in `README.md` and `RELEASE.md`.
- Update `AGENTS.md` package guidance if it mentions the split packages.

### P4: Optional naming cleanup

- Revisit `*InGraph` / `*ForData` class names and the remaining dialog
  families in a separate change.

## Risks and Mitigations

| Risk | Mitigation |
|------|------------|
| Import rewrite misses a reference | Compiler catches every unresolved reference; no warnings are introduced |
| `figure.java2d` classes are referenced from external JARs | No external deliverable references the internal package; verified by grep before shipping |
| Behavior drift during class collapse | Collapse one pair at a time; run the full suite between pairs |
| Coverage in the moved code is low | Construction / round-trip smoke tests exist for the most touched classes; extend per pair as needed |

## Verification

- `./mvnw clean test` green after every phase.
- `./mvnw spotless:check` green after every phase.
- JaCoCo re-measurement and `PROBLEMS.md` sync after P2 and P3.
