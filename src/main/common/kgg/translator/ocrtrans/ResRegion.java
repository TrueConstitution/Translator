package kgg.translator.ocrtrans;

public record ResRegion(int x, int y, int w, int h, String dst, String src) {
    public ResRegion scale(double scale) {
        return new ResRegion((int) (x * scale), (int) (y * scale), (int) (w * scale), (int) (h * scale), dst, src);
    }
}
