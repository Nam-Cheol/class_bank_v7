package com.tenco.bank.utils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

// abstract 을 쓰게 되면 직접적으로 new 할 수 없기 때문에 상속받아서 사용이 가능함
public abstract class ValueFormatter {

	public String timestampToString(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(timestamp);
	}
	
	public String formatKoreaWon(Long amount) {
		DecimalFormat df = new DecimalFormat("#,###");
		String formatNumber = df.format(amount);
		return formatNumber + "원";
	}
	
}
