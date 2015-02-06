package enduro;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Contestant {
	private long start;
	private long finish;
	private long result;

	public boolean addTime(long time) {
		if (start == 0) {
			start = time;
		} else if (finish == 0) {
			finish = time;
			calculateTotal();
		} else {
			return false;
		}
		return true;
	}

	private void calculateTotal() {
		result = finish - start;
	}

	public String getStart() {
		return timeToString(start);
	}

	public String getFinish() {
		if (finish == 0)
			return "--:--:--";
		return timeToString(finish);
	}

	private String timeToString(long time) {
		return new SimpleDateFormat("HH:mm:ss").format(new Date(time));
	}

	public String getResult() {
		if (result == 0)
			return "--:--:--";
		long temp = result;
		long hours = TimeUnit.MILLISECONDS.toHours(temp);
		temp -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(temp);
		temp -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(temp);
		temp -= TimeUnit.SECONDS.toMillis(seconds);


		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
}
