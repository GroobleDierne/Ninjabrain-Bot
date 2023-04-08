package ninjabrainbot.data.actions;

import ninjabrainbot.data.IDataState;
import ninjabrainbot.data.calculator.endereye.IThrow;
import ninjabrainbot.data.temp.IListComponent;
import ninjabrainbot.io.preferences.NinjabrainBotPreferences;

public class ChangeLastAngleAction implements IAction {

	private final IDataState dataState;
	private final NinjabrainBotPreferences preferences;
	private final boolean positive;

	public ChangeLastAngleAction(IDataState dataState, NinjabrainBotPreferences preferences, boolean positive) {
		this.dataState = dataState;
		this.preferences = preferences;
		this.positive = positive;
	}

	@Override
	public void execute() {
		if (dataState.locked().get())
			return;

		IListComponent<IThrow> throwList = dataState.getThrowSet();
		if (throwList.size() == 0)
			return;

		IThrow lastThrow = throwList.get(throwList.size() - 1);
		double newCorrection = lastThrow.correction() + getAngleCorrectionAmountInDegrees(lastThrow.beta());
		IThrow newThrow = lastThrow.withCorrection(newCorrection);

		throwList.replace(lastThrow, newThrow);
	}

	private double getAngleCorrectionAmountInDegrees(double beta) {
		double change = 0.01;
		if (preferences.useTallRes.get()) {
			final double toRad = Math.PI / 180.0;
			change = Math.atan(2 * Math.tan(15 * toRad) / preferences.resolutionHeight.get()) / Math.cos(beta * toRad) / toRad;
		}
		change *= positive ? 1 : -1;
		return change;
	}


}
