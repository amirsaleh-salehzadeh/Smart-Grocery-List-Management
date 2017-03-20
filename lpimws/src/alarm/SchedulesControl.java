package alarm;

import static java.util.concurrent.TimeUnit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import algorithm.apriori.Apriori;

public class SchedulesControl {
	private final ScheduledExecutorService scheduler = Executors
			.newSingleThreadScheduledExecutor();

	public void reCalculateData() {
		final Runnable calculator = new Runnable() {
			public void run() {
				Apriori apriori = new Apriori();
				apriori.calculateAssociationRules();
			}
		};
		final ScheduledFuture<?> scheduleHandle = scheduler.scheduleAtFixedRate(
				calculator, 7, 7, DAYS);
		scheduler.schedule(new Runnable() {
			public void run() {
				scheduleHandle.cancel(true);
			}
		}, 365, DAYS);
	}
}