package com.StarWars;

import java.util.Random;

import android.util.Log;

public class AttackEnemy {
	public int loop = 0;        // 루프 카운터
	private Random rnd = new Random();
	private int r1, r2;

	// ResetAttack - loop Clear
	public void ResetAttack() {
		loop = 0;
	}

	//  공격 명령
	public void Attack() {
		if (MyGameView.mMap.enemyCnt <= 10) {            // 적이 10명 이하면
			AttackAll();        // 일제 총공격
			return;
		}
		loop++;
		// 마지막 캐릭터의 delay시간에 120을 더한 값이 공격 시작 시간(루프)이다.
		int n = loop - (MyGameView.mMap.attackTime + 120);
		if (n < 0) return;                                               // 모든 캐릭터가 입장할 때까지 대기

		switch (n % 600) {
			case 0:
				// 맵 파일에 공격 경로가 11~20으로 만들어져 있다. 1~10의 난수를
				// 만들어서 10을 더한 값을 공격 경로로 사용한다. 10을 더하는 것을 Sprite()에서 처리한다.
				r1 = rnd.nextInt(10) + 1;
				AttackPath(3, 1, r1);                                    // 3등급, 1번기 : r1번 공격 루트로 출격
				AttackPath(3, 3, r1);
				AttackPath(2, 1, r1);
				break;
			case 50:
				r1 = rnd.nextInt(10) + 1;
				AttackPath(5, 4, r1);                                   // 5등급 4번기 : r1번 공격 루트로 출력
				AttackPath(5, 2, r1);
				AttackPath(4, 0, r1);
				break;
			case 100:
				r1 = rnd.nextInt(10) + 1;
				AttackPath(3, 0, r1);
				AttackPath(3, 2, r1);
				AttackPath(2, 4, r1);
				break;
			case 150:
				r1 = rnd.nextInt(10) + 1;
				AttackPath(0, 2, r1);
				AttackPath(1, 3, r1);
				AttackPath(1, 4, r1);
				break;
			case 200:
				r1 = rnd.nextInt(10) + 1;
				AttackPath(5, 3, r1);
				AttackPath(5, 5, r1);
				AttackPath(4, 6, r1);
				break;
			case 250:
				r1 = rnd.nextInt(10) + 1;
				AttackPath(3, 6, r1);
				AttackPath(3, 4, r1);
				AttackPath(2, 2, r1);
				break;
			case 300:
				r1 = rnd.nextInt(10) + 1;
				r2 = rnd.nextInt(10) + 1;
				AttackPath(2, 7, r1);
				AttackPath(2, 5, r1);
				AttackPath(0, 5, r2);
				AttackPath(1, 1, r2);
				break;
			case 350:
				r1 = rnd.nextInt(10) + 1;
				r2 = rnd.nextInt(10) + 1;
				AttackPath(4, 6, r1);
				AttackPath(4, 5, r1);
				AttackPath(3, 5, r1);
				AttackPath(3, 7, r2);
				AttackPath(4, 4, r2);
				break;
			case 400:
				r1 = rnd.nextInt(10) + 1;
				r2 = rnd.nextInt(10) + 1;
				AttackPath(5, 6, r1);
				AttackPath(5, 1, r1);
				AttackPath(2, 6, r2);
				AttackPath(2, 3, r2);
				break;
			case 450:
				r1 = rnd.nextInt(10) + 1;
				r2 = rnd.nextInt(10) + 1;
				AttackPath(1, 2, r1);
				AttackPath(1, 6, r1);
				AttackPath(2, 0, r2);
				AttackPath(4, 3, r2);
				break;
			case 500:
				r1 = rnd.nextInt(10) + 1;
				r2 = rnd.nextInt(10) + 1;
				AttackPath(4, 2, r1);
				AttackPath(4, 1, r1);
				AttackPath(1, 5, r2);
				AttackPath(5, 7, r2);
				break;
		}
	}

	//  적군의 공격
	private void AttackPath(int kind, int num, int aKind) {
		// 위에서 지시한 공격 루트로 이동
		//Sprite()에게 공격 경로를 전달한다.
		MyGameView.mEnemy[kind][num].BeginAttack(aKind);
	}

	//  남은 적군 총공격
	private void AttackAll() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				// 현재 공격 대형에서 대기 중인 캐릭터만 공격에 참가하므로 공격 중이거나, 공격을 끝낸 후 복귀 중인
				// 캐릭터는 공격에 참여하지 않는다.
				if (MyGameView.mEnemy[i][j].status == Sprite.SYNC)
					AttackPath(i, j, rnd.nextInt(10) + 1);   // 무작위 공격
			} // for j
		} // for i
	}

} // end Class
