package com.StarWars;

import android.util.Log;

public class DelayTime {
	private int Delay[][] = new int[6][8];

	// 생성자
	public DelayTime(String str) {

		//매개변수를 행단위로 나눈다.
		String tmp[] = str.split("\n");
		String s;
		int n;

		for (int i = 1; i < tmp.length; i++) {
			for (int j = 0; j < 8; j++) {
				// 행의 문자열을 4문자씩 잘라 좌우의 공백을 제거한다
				s = tmp[i].substring(j * 4, (j + 1) * 4).trim();
				if (s.equals("---")){
					Delay[i - 1][j] = -1;
				}else{
					Delay[i - 1][j] = Integer.parseInt(s);
				}
			} // for j
		} // for i
		Log.v("Delay Success", "Make Delay success");
	}

	// Get Delay
	public int GetDelay(int kind, int num) {
		return Delay[kind][num];
	}
}