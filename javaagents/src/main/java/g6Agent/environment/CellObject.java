package g6Agent.environment;

import g6Agent.services.ObjectType;

import java.util.concurrent.ConcurrentHashMap;


public class CellObject {
    private static final String TAG = "CellObject";
    private final ObjectType objectType;
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<ObjectType, CellObject>> cellContent;
    private int latest = -1;

    public CellObject(ObjectType objectType, ConcurrentHashMap<Integer, ConcurrentHashMap<ObjectType, CellObject>> cellContent) {
        this.objectType = objectType;
        this.cellContent = cellContent;
    }


    public ObjectType getObjectType() {
        return objectType;
    }

    public ConcurrentHashMap<ObjectType, CellObject> getLatestCellContent() {
        return cellContent.getOrDefault(latest, new ConcurrentHashMap<>()); // latest or this is an empty cell
    }

    @Override
    public String toString() {
        return TAG + " - " + objectType.toString();}

    public BlockObject getBlockObject() {
        CellObject block = this.getFirstByType(ObjectType.block);
        if (block instanceof BlockObject) {
            return (BlockObject) block;
        }
        return null;
    }

    private CellObject getFirstByType(ObjectType block) {
        return getLatestCellContent().get(block);
    }
}
