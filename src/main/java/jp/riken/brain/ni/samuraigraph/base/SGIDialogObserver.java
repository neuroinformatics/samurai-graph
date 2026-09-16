package jp.riken.brain.ni.samuraigraph.base;

/**
 * The common supertype of the observer contracts of the property dialogs.
 *
 * <p>Each property dialog defines its own type-safe observer interface for the values it edits and
 * the figure elements and data objects implement it. This marker only names that common role so
 * that the per-dialog observer types can be handled uniformly; it declares no methods and therefore
 * adds no contract to the implementors.
 */
public interface SGIDialogObserver {}
