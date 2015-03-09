package data;

import java.awt.event.KeyListener;

import main.Main;

public class EventData implements Common {
	/**
	 * EVENTのCSVデータ取得
	 * @param mapNo マップ番号
	 * @return String[] 対応したマップデータ
	 */
	public String[] getCsv(int mapNo){
		if(mapNo==0){
			return createEvent0();
		} else if (mapNo==1){
			return createEvent1();
		} else {
			return createEvent2();
		}
	}

	private String[] createEvent0() {
		String[] event0 = new String[15];
		//キャラ生成,x,y,キャラ番号,キャラの初期向き,動作設定,メッセージ
		event0[0] = "CHARA,5,2,1,2,0,なにしているの"+Main.id+"！もう城にまで魔物が来ているわ！早く魔王の所へむかって！";
		event0[1] = "CHARA,3,6,2,1,0,戦っている暇などありません。魔物はよけていきましょう！城内のピンクの髪の魔物に気を付けてくださいね。";
		event0[2] = "CHARA,16,6,3,0,1,落ちている赤い玉を拾うと足が速くなるらしいぞ。";
		event0[3] = "CHARA,18,2,4,1,1,黄色い玉は身を守る効果があるようだ！";
		event0[4] = "CHARA,25,6,5,2,0,城を出て上のルートは強い敵がいるがアイテムが多いって噂だ。";
		event0[5] = "CHARA,4,12,6,1,1,城を出て右のルートの方が森への近道らしいね！";
		event0[6] = "CHARA,15,27,7,0,1,追いかけてくる敵は離れるとあきらめてくれるみたいだ！";
		event0[7] = "CHARA,28,30,2,0,0,まずは森に向かってください。外には魔物がたくさんいますぞ、お気をつけて...。";
		//モンスター生成x,y,モンスター番号,キャラの初期向き,動作設定,スピード";
		event0[8] = "MONSTER,10,3,0,"+ DOWN +",2,1";
		event0[9] = "MONSTER,21,13,0,"+ UP +",2,1";
		event0[10] = "MONSTER,2,29,0,"+ UP +",2,1";
		//ムーブイベント,x,y,マップチップ番号,移動マップ,移動x,移動y
		event0[11] = "MOVE,29,31,18,1,6,57";
		//トレジャーイベント,x,y,アイテムID,マップチップ番号
		event0[12] = "TREASURE,18,5,0,20";
		event0[13] = "TREASURE,"+util.rnd(1, 3)+","+util.rnd(12,30)+",0,20";
		event0[14] = ((Main.clear_times<10&&Main.clear_times>=5)||(Main.clear_times>=15&&Main.clear_times<20)) ? "TREASURE,"+util.createTreasure(0, 0):"TREASURE,"+util.createTreasure(0, 1);
		return event0;
	}

	private String[] createEvent1(){
		String[] event1 = new String[34];
		//ムーブイベント,x,y,マップチップ番号,移動マップ,移動x,移動y
		event1[0] = "MOVE,6,57,13,0,29,30";
		event1[1] = "MOVE,32,36,7,2,0,60";
		event1[2] = "MOVE,38,36,7,2,63,60";
		//モンスター生成x,y,モンスター番号,キャラの初期向き,動作設定,スピード";
		//右ルート
		event1[3] = "MONSTER,27,57,1,3,2,2";
		event1[4] = "MONSTER,15,57,1,3,3,4";
		event1[5] = "MONSTER,60,51,0,3,2,2";
		event1[6] = "MONSTER,40,58,2,3,1,4";
		event1[20] = "MONSTER,17,29,4,1,2,2";
		event1[7] = "MONSTER,17,12,3,"+DOWN+",1,5";
		event1[8] = "MONSTER,32,17,2,4,2,2";
		event1[9] = "MONSTER,27,39,2,4,2,2";
		event1[33] = "MONSTER,33,46,4,1,2,2";
		//上ルート
		event1[10] = "MONSTER,6,49,0,2,2,2";
		event1[11] = "MONSTER,10,28,0,3,2,2";
		event1[12] = "MONSTER,6,40,1,3,4,4";
		event1[13] = "MONSTER,4,22,1,3,2,2";
		event1[14] = "MONSTER,3,20,1,3,4,4";
		event1[15] = "MONSTER,10,7,3,1,5,4";
		event1[16] = "MONSTER,28,4,4,1,2,2";
		event1[17] = "MONSTER,37,4,4,1,1,4";
		event1[18] = "MONSTER,50,4,4,1,2,4";
		event1[19] = "MONSTER,60,17,4,1,2,3";
		event1[21] = "MONSTER,51,33,2,1,4,4";
		event1[22] = "MONSTER,52,15,2,6,2,3";
		event1[23] = "MONSTER,39,15,2,1,1,4";
		event1[24] = "MONSTER,43,38,2,6,2,2";

		//トレジャーイベント,x,y,アイテムID,マップチップ番号
		event1[25] = "TREASURE,"+util.rnd(1, 11)+","+util.rnd(2,48)+",0,20";
		event1[26] = "TREASURE,"+util.rnd(14, 20)+","+util.rnd(17,45)+",0,20";
		event1[27] = "TREASURE,"+util.rnd(15, 42)+","+util.rnd(52,62)+",0,20";
		event1[28] = "TREASURE,"+util.rnd(49, 56)+","+util.rnd(10,27)+",0,20";
		event1[29] = "TREASURE,"+util.rnd(58, 62)+","+util.rnd(4,35)+",1,21";
		event1[30] = "TREASURE,"+util.rnd(40, 46)+","+util.rnd(32,38)+",0,20";
		event1[31] = (Main.clear_times>=15) ? "TREASURE,"+util.createTreasure(1, 1):"NULL";
		event1[32] = (Main.clear_times>=25) ? "TREASURE,"+util.createTreasure(1, 0):"NULL";
		return event1;
	}

	private String[] createEvent2(){
		String[] event2 = new String[18];
		//ムーブイベント,x,y,マップチップ番号,移動マップ,移動x,移動y
		event2[0] = "MOVE,60,39,15,0,3,2";
		event2[1] = "MOVE,0,60,3,1,32,36";
		event2[2] = "MOVE,63,60,3,1,38,36";
		//モンスター生成x,y,モンスター番号,キャラの初期向き,動作設定,スピード";
		event2[3] = "MONSTER,12,60,5,1,1,5";
		event2[4] = "MONSTER,52,60,5,1,1,5";
		event2[5] = "MONSTER,32,59,5,2,2,3";
		event2[6] = "MONSTER,48,40,6,3,2,4";
		event2[7] = "MONSTER,48,4,2,5,1,6";
		event2[8] = "MONSTER,53,40,0,1,2,3";
		//ドラゴン
		event2[9] = "MONSTER,3,24,9,3,2,5";
		event2[10] = "MONSTER,21,34,11,3,2,5";
		event2[11] = "MONSTER,26,7,13,3,2,5";
		event2[12] = "MONSTER,47,17,15,3,2,5";
		event2[13] = "MONSTER,61,10,9,3,2,5";
		//トレジャーイベント,x,y,アイテムID,マップチップ番号
		event2[14] = "TREASURE,49,39,0,20";
		event2[15] = "TREASURE,19,46,0,20";
		event2[16] = "TREASURE,24,16,0,20";
		event2[17] = (Main.clear_times>=30) ? "TREASURE,"+util.createTreasure(2, 1):"NULL";

		return event2;
	}

}
