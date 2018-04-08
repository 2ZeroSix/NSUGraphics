package model;

public interface MutableIsolineModel extends IsolineModel {
    void setParameters(Parameters parameters);
    void setFunction(Function2x2 function);
}
