package data;
/*
 *宝箱イベント管理
 */
public class TreasureEvent extends Event {
    // 宝箱に入っているアイテム名
    private String itemName;
    //宝箱に入っているアイテム番号
    private int itemNo;

    /*
     * @param x X座標
     * @param y Y座標
     * @param itemName 手に入るアイテム名
     */
    public TreasureEvent(int x, int y,int itemNo,int mapChipNo){
        // 宝箱のチップ番号は17でぶつからない
        super(x, y, mapChipNo, false);
        this.itemNo=itemNo;
    }

    /*
     * アイテム番号を返す
     */
    public int getItemNo() {
        return itemNo;
    }

    /*
     * イベントを文字列に変換（デバッグ用）
     */
    public String toString() {
        return "TREASURE:" + super.toString() + ":" + itemName;
    }
}
