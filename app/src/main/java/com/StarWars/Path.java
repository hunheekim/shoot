package com.StarWars;

import java.util.ArrayList;

import android.util.Log;

//---------------------------------
// Path ArrayList
//---------------------------------
public class Path {
	private ArrayList<SinglePath> mPath = new ArrayList<SinglePath>();

	//---------------------------------
	// 생성자
	//---------------------------------
	public Path(String str) {
		String tmp[] = str.split("\n");
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].indexOf("//") >= 0 || tmp[i].trim().equals("")) continue;
			mPath.add(new SinglePath(tmp[i]));
		}

		Log.v("Path", "Make path success");
	}

	//---------------------------------
	//  GetPath
	//---------------------------------
	public SinglePath GetPath(int index) {
		return mPath.get(index);
	}

}

//---------------------------------
// Single Path
//---------------------------------
class SinglePath {
	public int startX;			// 시작 좌표
	public int startY;
	public int dir[];			// 경로 방향
	public int len[];			// 경로 거리

	//---------------------------------
	//  생성자
	//---------------------------------
	public SinglePath(String str) {
		// str = " 0: 200,-30:  8-43, 9-3, 10-3, 11-3, 12-3, 13-3, 14-13";

		String tmp[] = str.split(":");   // tmp[1]: 시작 위치, tmp[2]: path
		// indexOf 해당 문자가 들어 있는 위치
		int n = tmp[1].indexOf(',');
		// trim 공백 문자 제거
		startX = Integer.parseInt(tmp[1].substring(0, n).trim());
		startY = Integer.parseInt(tmp[1].substring(n + 1).trim());

		String stmp[] = tmp[2].split(",");
		n = stmp.length;
		// 변수 선언부에서 만들어 둔 배열의 크기를 설정한다.
		dir = new int[n];
		len = new int[n];

		int p;
		for (int i = 0; i < n; i++) {
			//경로를 - 기준으로 방향과 거리로 구분해서 배열에 넣는다.
			p = stmp[i].indexOf('-');
			dir[i] = Integer.parseInt(stmp[i].substring(0, p).trim());
			len[i] = Integer.parseInt(stmp[i].substring(p + 1).trim());
		}
	}

}
