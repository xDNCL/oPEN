package pen.LilyPadSimulatorGUI_Level1;

public interface penPlugin {
	/**
	 * プログラムの実行ボタンが押された時に呼び出されるメソッド
	 * ここのメソッドにプラグインの初期化処理などを記述してください
	 */
	void init();
	
	/**
	 * プログラム実行終了時に呼び出されるメソッド
	 * ここのメソッドにプラグインの最終処理を記述してください
	 */
	void destruction();
}
