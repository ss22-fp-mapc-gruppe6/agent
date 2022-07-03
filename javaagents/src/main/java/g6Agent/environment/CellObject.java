package g6Agent.environment;

import g6Agent.services.ObjectType;

/**
 * Unused
 */
public class CellObject {
    private static final String TAG = "CellObject";
    private final ObjectType objectType;


    CellObject(ObjectType objectType) {
        this.objectType = objectType;
    }

    public ObjectType getObjectType() {
        return objectType;
    }


    @Override
    public String toString() {
        return TAG + " - " + objectType.toString();
    }
}
