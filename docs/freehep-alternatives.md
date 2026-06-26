# FreeHEP Library Migration Analysis

## Current Usage

FreeHEP libraries are used for vector graphics export in the following components:

### SGImageExportManager.java
- **PDF export:** `PDFGraphics2D`
- **SVG export:** `SVGGraphics2D`
- **EMF export:** `EMFGraphics2D`
- **PostScript export:** `PSGraphics2D`
- **Export dialog:** `ExportDialog`, `ExportFileType`, `ExportFileTypeRegistry`
- **Constants:** `ImageConstants`, `PageConstants`, `FontConstants`, `InfoConstants`

### Custom Export File Types
- `PNGExportFileType` extends `ImageExportFileType`
- `JPEGExportFileType` extends `ImageExportFileType`

### Custom Registry
- `ExportFileTypeRegistry.java` - Custom implementation to work around library bugs

## Alternative Libraries

### Apache XMLGraphics
- **PDF:** Apache XMLGraphics Commons provides PDF generation
- **PS:** PostScript support available
- **Status:** Actively maintained, Apache 2.0 license

### Apache Batik
- **SVG:** Full SVG 1.1/1.2 support
- **Status:** Actively maintained, Apache 2.0 license

### Java2D Native
- **Image export:** Built-in JPEG/PNG/GIF support via `javax.imageio`
- **Status:** No external dependency needed

## Migration Strategy

| Format | Current (FreeHEP) | Alternative | Effort |
|--------|------------------|-------------|--------|
| PDF | PDFGraphics2D | Apache PDFBox or iText | High |
| SVG | SVGGraphics2D | Apache Batik | Medium |
| EMF | EMFGraphics2D | No direct alternative | High (consider deprecating) |
| PS | PSGraphics2D | Apache XMLGraphics | High |
| PNG/JPEG | ImageExportFileType | javax.imageio | Low |

## Recommendations

1. **Short term:** Keep FreeHEP for PDF/SVG/EMF/PS, remove only SWF (already done in TASK-011)
2. **Medium term:** Replace PNG/JPEG export with javax.imageio (low effort)
3. **Long term:** Evaluate Apache Batik for SVG and Apache PDFBox for PDF
4. **Consider deprecating:** EMF format is Windows-specific and rarely used

## Risk Assessment

- **High risk:** Full FreeHEP replacement requires significant testing
- **Medium risk:** SVG migration via Batik is well-documented
- **Low risk:** Image export migration to javax.imageio

## Estimated Effort

- PNG/JPEG migration: 1-2 days
- SVG migration: 3-5 days
- PDF migration: 5-7 days
- EMF deprecation: 1 day
- PS migration: 3-5 days

## Conclusion

Given the complexity and risk, it is recommended to keep FreeHEP for vector graphics export (PDF, SVG, EMF, PS) while migrating image export (PNG, JPEG) to javax.imageio. Full FreeHEP replacement should be considered a long-term goal.
