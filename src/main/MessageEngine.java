package main;
import java.awt.*;

/**
 * 文字からフォント画像に変換
 */

public class MessageEngine {
	//フォントの大きさ
	public static final int FONT_WIDTH=16;
	public static final int FONT_HEIGHT=22;

	//フォントの色
	public static final int WHITE=0;
	public static final int RED=160;
	public static final int GREEN=320;
	public static final int BLUE=480;

	//色
	private int color;

	public MessageEngine(){
		color=WHITE;
	}

	public void setColor(int c){
		this.color=c;
		//変な値だったらWHITEにする
		if(color!=WHITE&&color!=RED&&color!=GREEN&&color!=BLUE){
			this.color=WHITE;
		}
	}
	/*
	 * メッセージを描画する
	 * @param x X座標
	 * @param y Y座標
	 * @param message メッセージ
	 * @param g 描画オブジェクト
	 */
	public void drawMessage(int x, int y,String message,Graphics g){
		for(int i=0;i<message.length();i++){
			char c=message.charAt(i);    //cをイメージのフォントに
			int dx=x+FONT_WIDTH*i; //今描画したやつの隣にどんどん
			drawCharacter(dx,y,c,g);
		}
	}
	/*
	 * 文字を描画
	 *  @param x X座標
     * @param y Y座標
     * @param c 文字
     * @param g 描画オブジェクト
	 */
	public void drawCharacter(int x,int y,char c,Graphics g){
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.SERIF,Font.BOLD+Font.PLAIN,15));
		g.drawString(String.valueOf(c), x, y);
	}
}
