package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;
    private int tickFactor;

    public Data(Type type, int size) {
        this.type = type;
        switch (type) {
            case Text:
                tickFactor = 2;
                break;
            case Images:
                tickFactor = 4;
                break;
            case Tabular:
                tickFactor = 1;
                break;
        }
        processed = 0;
        this.size = size;
    }

    public Data.Type getType() { return type; }

    public String getTypeS() {
    if(type==type.Images)
        return "Images";
    else if (type==type.Tabular)
        return "Tabular";
    else return "Text";
    }

    public int getSize() { return size; }

    public int getTickFactor() { return tickFactor; }
}
