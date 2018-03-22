package ru.nsu.ccfit.lukin.model.filters;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class FilterFactoryImpl implements FilterFactory {
    public static final String BlACK_WHITE              = "black and white";
    public static final String NEGATIVE                 = "negative";
    public static final String DOUBLE                   = "double";
    public static final String FLOYD_STEINBERG_DITHER   = "floyd-steinberg dithering";
    public static final String ORDERED_DITHER           = "ordered dithering";
    public static final String ROBERTS_OPERATOR         = "roberts operator";
    public static final String SOBEL_OPERATOR           = "sobel operator";
    public static final String SMOOTH_FILTER            = "smooth filter";
    public static final String SHARP_FILTER             = "sharp filter";
    public static final String EMBOSS_FILTER            = "emboss filter";
    public static final String WATERCOLOR_FILTER        = "watercolor filter";
    public static final String VOLUME_RENDERING         = "volume rendering";

    @Override
    public Filter getFilter(String filterName, String... args) {
        switch (filterName) {
            case BlACK_WHITE: {
                return new BlackAndWhiteFilter();
            }
            case NEGATIVE: {
                return new NegativeFilter();
            }
            case FLOYD_STEINBERG_DITHER: {
                if (args.length != 3) {
                    throw new IllegalArgumentException("wrong number of arguments for " + FLOYD_STEINBERG_DITHER);
                }
                int countR = Integer.valueOf(args[0]);
                if (countR < 0 || countR > 256) throw new IllegalArgumentException("wrong Nr: " + countR);
                int countG = Integer.valueOf(args[1]);
                if (countG < 0 || countG > 256) throw new IllegalArgumentException("wrong Ng: " + countG);
                int countB = Integer.valueOf(args[2]);
                if (countB < 0 || countB > 256) throw new IllegalArgumentException("wrong Nb: " + countB);
                return new FloydSteinbergDithering(countR, countG, countB);
            }
            case ORDERED_DITHER: {
                if (args.length != 3) {
                    throw new IllegalArgumentException("wrong number of arguments for " + FLOYD_STEINBERG_DITHER);
                }
                int countR = Integer.valueOf(args[0]);
                if (countR < 0 || countR > 256) throw new IllegalArgumentException("wrong Nr: " + countR);
                int countG = Integer.valueOf(args[1]);
                if (countG < 0 || countG > 256) throw new IllegalArgumentException("wrong Ng: " + countG);
                int countB = Integer.valueOf(args[2]);
                if (countB < 0 || countB > 256) throw new IllegalArgumentException("wrong Nb: " + countB);
                return new OrderedDithering(countR, countG, countB);
            }
            case DOUBLE: {
                return new NearestNeighborDoubleFilter();
            }
            case ROBERTS_OPERATOR: {
                // TODO ROBERTS_OPERATOR
                if (args.length != 1) {
                    throw new IllegalArgumentException("wrong number of arguments for " + SOBEL_OPERATOR);
                }
                int threshold = Integer.valueOf(args[0]);
                if (threshold < 0 || threshold > 256) throw new IllegalArgumentException("wrong threshold: " + threshold);
//                return new RobertsFilter(threshold);
                throw new NotImplementedException();
            }
            case SOBEL_OPERATOR: {
                if (args.length != 1) {
                    throw new IllegalArgumentException("wrong number of arguments for " + SOBEL_OPERATOR);
                }
                int threshold = Integer.valueOf(args[0]);
                if (threshold < 0 || threshold > 256) throw new IllegalArgumentException("wrong threshold: " + threshold);
                return new SobelFilter(threshold);
            }
            case SMOOTH_FILTER: {
                // TODO SMOOTH_FILTER
                throw new NotImplementedException();
            }
            case SHARP_FILTER: {
                // TODO SHARP_FILTER
                throw new NotImplementedException();
            }
            case EMBOSS_FILTER: {
                // TODO EMBOSS_FILTER
                throw new NotImplementedException();
            }
            case WATERCOLOR_FILTER: {
                // TODO WATERCOLOR_FILTER
                throw new NotImplementedException();
            }
            default: {
                throw new NotImplementedException();
            }
        }
    }

    @Override
    public String getFilterName(Filter filter) {
        if (filter instanceof BlackAndWhiteFilter) {
            return BlACK_WHITE;
        } else if (filter instanceof NegativeFilter) {
            return NEGATIVE;
        } else if (filter instanceof NearestNeighborDoubleFilter) {
            return DOUBLE;
        } else if (filter instanceof FloydSteinbergDithering) {
            return FLOYD_STEINBERG_DITHER;
        } else if (filter instanceof OrderedDithering) {
            return ORDERED_DITHER;
        } else if (filter instanceof RobertsFilter) {
            return ROBERTS_OPERATOR;
        } else if (filter instanceof SobelFilter) {
            return SOBEL_OPERATOR;
        } else if (filter instanceof SmoothFilter) {
            return SMOOTH_FILTER;
        }
        return null;
    }
}
