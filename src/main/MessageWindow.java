package main;
import java.awt.*;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


/*
 *メッセージウインドウ管理
 */
public class MessageWindow {
    // 白枠の幅
    private static final int EDGE_WIDTH = 2;

    // 行間の大きさ
    protected static final int LINE_HEIGHT = 8;
    // 1行の最大文字数
    private static final int MAX_CHAR_IN_LINE = 35;
    // 1ページに表示できる最大行数
    private static final int MAX_LINES = 2;
    // 1ページに表示できる最大文字数
    private static final int MAX_CHAR_IN_PAGE = MAX_CHAR_IN_LINE * MAX_LINES;

    // 一番外側の枠
    private Rectangle rect;
    // 一つ内側の枠（白い枠線ができるように）
    private Rectangle innerRect;
    // 実際にテキストを描画する枠
    private Rectangle textRect;

    // メッセージウィンドウを表示中か
    private boolean isVisible = false;

    // カーソルのアニメーションGIF
    private Image cursorImage;
    // メッセージを格納した配列
    private char[] text = new char[128 * MAX_CHAR_IN_LINE];
    // 最大ページ
    private int maxPage;
    // 現在表示しているページ
    private int curPage = 0;

    //表示した文字数
    private int curPos;

    //次のページへいけるか（▼が表示されてたら行ける（true））
    private boolean nextFlag=false;

    // メッセージエンジン
    private MessageEngine messageEngine;

    //テキストを流すタイマータスク
    	private Timer timer;
    	private TimerTask task;


    	//インスタンス生成
    	public MainPanel panel;

    public MessageWindow(Rectangle rect) {
        this.rect = rect;

        innerRect = new Rectangle(rect.x + EDGE_WIDTH,rect.y + EDGE_WIDTH, rect.width - EDGE_WIDTH * 2, rect.height - EDGE_WIDTH * 2);

        textRect = new Rectangle(innerRect.x + 16,innerRect.y + 16,320,120);

        // メッセージエンジンを作成
        messageEngine = new MessageEngine();
        ImageIcon icon = new ImageIcon(getClass().getResource("image/cursor.gif"));
        cursorImage = icon.getImage();


        timer=new Timer();
    }

    public void draw(Graphics g) {
        if (isVisible == false) return;

        // 枠を描く
        g.setColor(Color.WHITE);
        g.drawRect(rect.x, rect.y, innerRect.width ,innerRect.height);
        // 内側の枠を描く
        g.setColor(new Color(0,0,0,150));
        g.fillRect(innerRect.x, innerRect.y, innerRect.width, innerRect.height);

        // 現在のページ（curPage）のcurPosまでの内容を表示
        for (int i=0; i<curPos; i++) {
            char c = text[curPage * MAX_CHAR_IN_PAGE + i];
            int dx = textRect.x + MessageEngine.FONT_WIDTH * (i % MAX_CHAR_IN_LINE);
            int dy = 12 + textRect.y + (LINE_HEIGHT + MessageEngine.FONT_HEIGHT) * (i / MAX_CHAR_IN_LINE);
            messageEngine.drawCharacter(dx, dy, c, g);
        }

        // 最後のページでない場合は矢印を表示する
        if (curPage < maxPage) {
            int dx = textRect.x + (MAX_CHAR_IN_LINE / 2) * MessageEngine.FONT_WIDTH - 8;
            int dy = textRect.y + (LINE_HEIGHT + MessageEngine.FONT_HEIGHT) * 3;
            g.drawImage(cursorImage, 390,450, null);
        }
    }
    /*
     * メッセージをセットする
     * @param msg メッセージ
     */
    public void setMessage(String msg) {
        curPos=0;		//curPos文字まで描画　というサイン 増やしていって流す
    	curPage = 0;
    	nextFlag=false;

        // 全角スペースで初期化
        for (int i=0; i<text.length; i++) {
            text[i] = '　';
        }

        int p = 0;  // 処理中の文字位置
        for (int i=0; i<msg.length(); i++) {
            char c = msg.charAt(i);
            if (c == '\\') {
                i++;
                if (msg.charAt(i) == 'n') {  // 改行
                    p += MAX_CHAR_IN_LINE;
                    p = (p / MAX_CHAR_IN_LINE) * MAX_CHAR_IN_LINE;
                } else if (msg.charAt(i) == 'f') {  // 改ページ
                    p += MAX_CHAR_IN_PAGE;
                    p = (p / MAX_CHAR_IN_PAGE) * MAX_CHAR_IN_PAGE;
                }
            } else {
                text[p++] = c;
            }
        }
        maxPage = p / MAX_CHAR_IN_PAGE;

        //文字を流すタスクを起動
        task=new DrawingMessageTask();
        timer.schedule(task,0L,50L);//0L(直後)に起動してから50L（５０ミリ秒）毎にtaskを実行
    }
    /*
     * メッセージを先に進める
     * @return メッセージが終了したらtrueを返す
     */
    public boolean nextMessage() {
        // 現在ページが最後のページだったらメッセージを終了する
        if (curPage == maxPage) {
        	task.cancel();
        	task=null;//タスクは終了してないと動き続ける
            return true;
        }
        //▼が表示されていなければ次のページへいけない
        if(nextFlag){
        curPage++;
        curPos=0;
        nextFlag=false;
        }
        return false;
    }

    /*
     * ウィンドウを表示
     */
    public void show() {
        isVisible = true;
    }

    /*
     * ウィンドウを隠す
     */
    public void hide() {
        isVisible = false;
    }

    /*
     * ウィンドウを表示中か
     */
    public boolean isVisible() {
        return isVisible;
    }
    // メッセージを1文字ずつ順に描画するタスク
    class DrawingMessageTask extends TimerTask {
        public void run() {
            if (!nextFlag) {
                curPos++;  // 1文字増やす
                // 1ページの文字数になったら▼を表示
                if (curPos % MAX_CHAR_IN_PAGE == 0) {
                    nextFlag = true;
                }
            }
        }
    }


}
