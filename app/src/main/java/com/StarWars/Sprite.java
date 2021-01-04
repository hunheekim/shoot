package com.StarWars;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

//--------------------------------
// 적군 캐릭터
//--------------------------------
public class Sprite {
	final static int ENTER = 1;			// 캐릭터 입장
	final static int BEGINPOS = 2;		// 전투 대형으로 가기 위해 좌표 계산
	final static int POSITION = 3;		// 전투 대형으로 이동 중
	final static int SYNC = 4;			// 전투 대형에서 대기중
	final static int ATTACK = 5;		// 공격중
	final static int BEGINBACK = 6;		// View를  벗어나서 다시 입장할 준비
	final static int BACKPOS = 7;		// 다시 입장 중

	public int x, y;					// 캐릭터의 좌표
	public int w, h;					// 크기
	public boolean isDead;				// 전사자인가?
	public int shield;					// 보호막
	public int status;					// 상태

	public Bitmap imgSprite;			// 현방향의 이미지

	private SinglePath sPath; 			// 캐럭터가 이동할 Path 1줄 (입장 및 공격 루트)
	private float sx, sy;				// 캐릭터 이동 속도
	private int sncX;					// 싱크 위치로부터 떨어져 있는 거리
	private Bitmap imgSpt[] = new Bitmap[16];
	private int sKind, sNum;			// 캐릭터의 종류와 번호
	private int pNum, col;				// Path 번호와 현재의 경로
	private int delay, dir, len;		// 입장시 지연시간, 현재의 방향, 남은 거리
	private int posX, posY;				// 이동해야 할 목적지 좌표
	private int aKind;					// 공격 루트 번호
	private Random rnd = new Random();

	private int diff[] = {8, 6, 4};		// EASY, MEDIUM, HARD
	private int df;						// 난이도

	//--------------------------------
	// 생성자
	//--------------------------------
	public Sprite() {
		// 내용 없음
	}

	//--------------------------------
	// Sprite 만들기
	//--------------------------------
	public void MakeSprite(int kind, int num) {
		//자신의 종류와 번호를 보존한다. 종류는 0~5, 번호는 0~7 사이의 값이다. 캐릭터의 종류와 번호는 MyGameView에서
		//mSprite[i][j].MakeSprite(i, j);와 같은 형식으로 지정
		sKind = kind;
		sNum = num;

		// 불필요한 캐릭터
		if (MyGameView.mMap.GetSelection(kind, num) == -1) {
			isDead = true;
			return;
		}
		//맵의 position 섹션에서 지정한 캐릭터 이미지 번호를 구한 후 해당 이미지를 비트맵에 읽어 들인다
		int enemy =  MyGameView.mMap.GetEnemyNum(kind, num);
		imgSpt[0] = BitmapFactory.decodeResource(MyGameView.mContext.getResources(), R.drawable.enemy00 + enemy);

		int sw = imgSpt[0].getWidth();
		int sh = imgSpt[0].getHeight();
		w = sw / 2;
		h = sh / 2;

		// 16방향으로 회전한 이미지 만들기
		// 비트맵을 22.5° 간격으로 회전시켜 16방향의 이미지를 만들 Canvas를 준비
		Canvas canvas = new Canvas();
		for (int i = 1; i < 16; i++) {
			imgSpt[i] = Bitmap.createBitmap(sw, sh, Config.ARGB_8888);  // 캐릭터 크기와 같은 빈 비트맵을 만든다(①)
			canvas.setBitmap(imgSpt[i]);	// Canvas에 ①의 빈 비트맵을 올려놓는다
			canvas.rotate(22.5f, w, h);	//Canvas를 시계 방향으로 22.5°(1/16 방향) 회전한다.
			canvas.drawBitmap(imgSpt[0], 0, 0, null); //회전한 Canvas에 원본 이미지를 출력한다
		}
		ResetSprite();	//캐릭터의 변수를 초기화시키는 부분
	}

	//--------------------------------
	// Reset Sprite
	//--------------------------------
	public void ResetSprite() {
		pNum = MyGameView.mMap.GetSelection(sKind, sNum);	// Path 번호
		delay = MyGameView.mMap.GetDelay(sKind, sNum);		// Delay 시간 읽기
		shield = MyGameView.mMap.GetShield(sKind, sNum);	// 보호막 읽기

		posX = MyGameView.mMap.GetPosX(sKind, sNum);		// 전투대형 위치
		posY = MyGameView.mMap.GetPosY(sKind, sNum);

		GetPath(pNum);										// pNum으로 구한 Path 읽기
		status = ENTER;
		isDead = false;
		df = MyGameView.difficult;			// 게임 난이도 - 총알 발사빈도

	}

	//--------------------------------
	// Path - Path 1줄 읽기
	//--------------------------------
	public void GetPath(int num) {
		sPath = MyGameView.mMap.GetPath(num);	// Path 읽기
		// Path의 시작 좌표
		// 맵에 기록해 뒀던 캐릭터의 시작 좌표이다. 이 값이 -99인 경우 공격 경로로 사용하기로 했는데,
		// 공격 경로의 시작 위치는 캐릭터의 현재의 위치가 된다.
		if (sPath.startX != -99)
			x = sPath.startX;
		if (sPath.startY != -99)
			y = sPath.startY;
		col = 0;
		GetDir(col);
	}

	// GetDir - 현위치의 방향과 거리
	// 현재의 방향과 이동할 거리를 계산하는 부분이다.
	// col이 배열의 첨자(Subscript) 역할을 한다.
	private void GetDir(int col) {
		dir = sPath.dir[col];			// 이동할 방향
		len = sPath.len[col];			// 이동할 거리

		// 캐릭터의 이동 속도는 맨 처음에 MapTable()을 작성할 때 16 방향 삼각함수를 계산해서
		// 배열에 저장해 둔 것을 읽어오는 것이다
		sx = MyGameView.mMap.sx[dir];	// 이동 속도
		sy = MyGameView.mMap.sy[dir];
		imgSprite = imgSpt[dir];		// 현방향의 이미지
	}

	//--------------------------------
	// Move
	//--------------------------------
	public void Move() {
		if (isDead && (sKind != 5 || sNum != 0)) return;

		switch (status) {
			case ENTER :			// 캐릭터 입장
				EnterSprite();
				break;
			case BEGINPOS:			// 전투 대형 위치 계산
				BeginPos();
				break;
			case POSITION :			// 전투 대형 위치로 이동중
				Position();
				break;
			case SYNC :				// 전투 대형 위치에서 대기 중
				MakeSync();
				break;
			case ATTACK :			// 공격중
				Attack();
				break;
			case BEGINBACK :		// 탈영병 복귀 준비 중
				BeginBackPos();
				break;
			case BACKPOS :			// 탈영병 복귀 중
				BackPosition();
		}
	}

	//--------------------------------
	// Enter Sprite
	//--------------------------------
	public void EnterSprite() {
		if (--delay >= 0) return;

		x += (int) (sx * 8);
		y += (int) (sy * 8);

		// 캐릭터 입장시 아군을 공격할 방향 결정
		int dr = rnd.nextInt(5) + 6;   // 6~10: 발사 방향
		if (len % 15 == 0)
			ShootMissile(dr);

		len--;
		if (len >= 0) return;

		col++;
		if (col < sPath.dir.length) {
			GetDir(col);					// 다음 경로 찾기
		}
		else {
			status = BEGINPOS;		// 경로의 끝이면 전투 대형으로 이동
		}
	}

	//--------------------------------
	// BeginPos - 전투 대형으로 이동 준비
	//--------------------------------
	public void BeginPos() {
		// 원래의 Path 읽기
		if (x < posX + MyGameView.mMap.syncCnt)			// 이동 방향 결정
			dir = 2;									// 북동(NW)쪽
		else
			dir = 14;									// 북서(NW)쪽

		if (y < posY)
			dir = (dir == 2) ? 6 : 10;

		sx = MyGameView.mMap.sx[dir];					// 이동 방향에 따른 속도 계산
		sy = MyGameView.mMap.sy[dir];
		imgSprite = imgSpt[dir];						// 현 방향의 이미지
		status = POSITION;								// 목적지로 이동 준비 끝
	}

	//--------------------------------
	// Position - 전투 대형으로 이동 중
	//--------------------------------
	public void Position() {
		x += (int) (sx * 8);							// 이동
		y += (int) (sy * 8);

		// 싱크 때문에 목적지가 멀어졌을 수 있으므로 방향 다시 계산
		if (x < posX + MyGameView.mMap.syncCnt)			// 이동 방향 결정
			dir = 2;									// 북동(NW)쪽
		else
			dir = 14;									// 북서(NW)쪽

		if (y < posY)
			dir = (dir == 2) ? 6 : 10;

		// 수평 좌표 비교
		if (Math.abs(y - posY) <= 4) {					// 수평 위치 도착
			y = posY;
			if (x < posX + MyGameView.mMap.syncCnt)		// 좌우 방향 결정
				dir = 4;								// 3시방향
			else
				dir = 12;								// 9시 방향
		}

		// 수직 좌표 비교
		if (Math.abs(x - (MyGameView.mMap.syncCnt + posX)) <= 4) {
			x = posX + MyGameView.mMap.syncCnt;
			dir = 0;						// 12시 방향
		}

		if (y == posY && x == posX + MyGameView.mMap.syncCnt) {
			imgSprite = imgSpt[0];			// 전투대형 위치 도착
			sx = 1;
			status = SYNC;					// 좌우로 이동하며 공격 명령 대기
			return;							// 싱크 유지 중
		}

		sx = MyGameView.mMap.sx[dir];		// 위에서 설정한 전투 대형 위치로
		sy = MyGameView.mMap.sy[dir];		// 계속 이동
		imgSprite = imgSpt[dir];
	}

	//--------------------------------
	// Sync & Move - 싱크를 유지하며 이동
	//--------------------------------
	public void MakeSync() {
		sncX = (int) MyGameView.mMap.sx[MyGameView.mMap.dir];	// 좌우 이동방향 계산
		x += sncX;								// 좌 또는 우로 이동


		// Sync 설정
		// 싱크의 기준은 맨처음 공격 대형에 진입한 5레벨 0번 캐릭터이다.
		// 이 캐릭터만MapTable()의 변수에 값을 써 놓을 수 있다.
		if (sKind == 5 && sNum == 0) {			// 이 캐릭터가 싱크를 설정한다
			MyGameView.mMap.syncCnt += sncX;	// 최초 도착자가 좌우로 이동한 거리
			MyGameView.mMap.dirCnt++;			// 현재 방향으로 이동한 거리
			if (MyGameView.mMap.dirCnt >= MyGameView.mMap.dirLen) {
				MyGameView.mMap.dirCnt = 0;		// 현재 방향의 끝에 도착
				// 최초의 방향으로 이동 후 반대 방향으로 전환할 때에는 원래 값의 2배수를 적용한다.
				// 즉, 화면 중심으로부터 동쪽으로 40이동했으면 그 후로는 서 80, 동 80, 서80, 의 순서로 이동해야 좌우의 균형이 맞는다.
				MyGameView.mMap.dirLen = 104;	// 반대 방향으로 이동할 거리
				MyGameView.mMap.dir = 16 - MyGameView.mMap.dir;	// 이동방향 반전
			}
		}
	}

	// Begin Attack - 공격 루트 수령
	// 공격 루트(경로)는 매개변수로 전달 받는다.
	public void BeginAttack(int aKind) {
		if (isDead || (sKind == 5 && sNum == 0)) return;	// 싱크 기준은
		this.aKind = aKind;			//공격 경로 번호를 설정한다. MyGameview에서 공격 루트를 1,2,3으로 설정하면 실제의 공격 경로는 11, 12, 13이다.
		GetPath(aKind + 10);		  //현재 공격 상태임을 Move() 메서드에 알린다.
		status = ATTACK;
	}

	//--------------------------------
	// Attack
	//--------------------------------
	public void Attack() {
		x += (int) (sx * 8);			// 공격 루트로 비행중
		y += (int) (sy * 8);

		//비행 중 전투 위치를 이탈
		//캐릭터의 좌표를 이동한 후 View로부터 일정한 거리를 벗어나면 복귀 명령을 내린다.
		if (y < - 164 || y > MyGameView.Height + 164 ||
				x < -164 || x > MyGameView.Width + 164) {
			status = BEGINBACK;			// 원대 복귀 준비
			return;
		}

		len--;
		if (len >= 0) return;			// 현재 방향으로 계속 이동 중

		col++;							// 방향 전환
		if (col < sPath.dir.length) {
			//캐릭터가 방향을 전환한 후 6~10의 방향이면 그 방향으로 미사일을 발사한다.
			GetDir(col);				// 방향 전환 후 공격 시작
			if (dir >=  6 && dir <= 10)
				ShootMissile(dir);
		}
		else {
			//공격 경로의 끝까지 이동한 후 전투 대형 위치로 돌아간다.
			status = BEGINPOS;			// 공격을 끝내고 끝났으면 전투 대형으로 복귀
		}
	}

	//--------------------------------
	// 탈영병 복귀 준비
	//--------------------------------
	public void BeginBackPos() {
		// GetPath(pNum);
		y = -32;												// 복귀 시작점 (View의 상단)
		x = posX + MyGameView.mMap.syncCnt;						// 자신의 전투 대형 위치 계산

		imgSprite = imgSpt[0];									// 북쪽을 바라보는 자세로 입장
		status = BACKPOS;
	}

	//--------------------------------
	// View의 위에서 전투 대형 위치로 이동
	//--------------------------------
	public void BackPosition() {
		// 전투 대형이 좌우 어느 쪽으로 이동 중인가 계산
		// 위에서 내려올 때 싱크 맞추기
		sncX = (int) MyGameView.mMap.sx[MyGameView.mMap.dir];
		y += 2;			// 입장 속도는 2
		x += sncX;		// 전투 대형의 이동 방향과 맞추어 좌우로 이동하며 입장

		// 전투대형 복귀 후 마지막 공격 루트로 다시 공격 시작
		// 전투 대형으로 복귀한 후 곧바로 다시 공격에 참여하도록 설정한다.
		if (Math.abs(y - posY) <= 4) {
			GetPath(aKind + 10);
			status = ATTACK;
		}
	}

	//--------------------------------
	// Shoot Missile - 미사일 발사
	//--------------------------------
	private void ShootMissile(int dir) {
		if (rnd.nextInt(10) >= diff[df])
			MyGameView.mMissile.add(new Missile(x, y, dir));
	}

}
