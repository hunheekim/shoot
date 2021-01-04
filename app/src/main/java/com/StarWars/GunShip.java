package com.StarWars;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GunShip {
	public int x, y;				// 위치
	public int w, h;				// 폭과 높이
	public int shield;				// 보호막
	public int dir;					// 이동 방향(1:왼쪽, 2:오른쪽, 3, 위쪽, 0:정지)
	public boolean isDead;			// 사망
	public boolean undead;			// 무적 모드
	public int undeadCnt;			// 무적 지속 시간
	public Bitmap imgShip;			// 우주선 이미지

	private Bitmap imgTemp[] = new Bitmap[8];

	private int sx[] = {0, -8, 8, 0};
	private int sy[] = {0, 0, 0, -8};

	private int imgNum = 0;				// 이미지 번호

	//--------------------------------
	// 생성자
	//--------------------------------
	public GunShip(int x, int y) {
		this.x = x;
		this.y = y;
		for (int i = 0; i < 8; i++)
			imgTemp[i] = BitmapFactory.decodeResource(MyGameView.mContext.getResources(), R.drawable.gunship0 + i);

		w = imgTemp[0].getWidth() / 2;
		h = imgTemp[0].getHeight() / 2;

		ResetShip();
	}

	//--------------------------------
	// 건쉽 초기화
	//--------------------------------
	public void ResetShip() {
		//우주선이 초기에 나타날 View 위치
		x = MyGameView.Width / 2;
		y = MyGameView.Height - 36;
		shield = 3;
		isDead = false;
		undeadCnt = 50;			// 무적 시간
		undead = true;				// 초기설정 무적
		dir = 0;
		imgShip = imgTemp[0];
	}

	//--------------------------------
	// Move
	//--------------------------------
	public boolean Move() {
		imgNum++;
		//애니메이션용 이미지 번호
		if (imgNum > 3) imgNum = 0;

		// 우주선 모양
		if (undead) {
			imgShip = imgTemp[imgNum + 4];
			undeadCnt--;
			if (undeadCnt < 0) undead = false;
		} else {
			imgShip = imgTemp[imgNum];
		}

		// 우주선 이동
		x += sx[dir];
		y += sy[dir];

		if (x < w) {						// View의 왼쪽 끝
			x = w;
			dir = 0;
		} else if (x > MyGameView.Width - w) {
			x = MyGameView.Width - w;
			dir = 0;
		}
		//스테이지를 Clear하고 우주선이 View의 위로 올라갈 때 View의 끝까지 이동했는지 판단용
		return (y < -32);		// Stage Clear용
	}
}
