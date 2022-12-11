package ninjabrainbot.data.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import ninjabrainbot.data.datalock.AlwaysUnlocked;
import ninjabrainbot.data.divine.DivineContext;
import ninjabrainbot.data.divine.IDivineContext;
import ninjabrainbot.data.stronghold.Chunk;
import ninjabrainbot.data.stronghold.Ring;
import ninjabrainbot.io.preferences.MultipleChoicePreferenceDataTypes.McVersion;
import ninjabrainbot.util.TestUtils;

class RayApproximatedPriorTests {

	IDivineContext divineContext;

	@BeforeEach
	void setup() {
		divineContext = new DivineContext(new AlwaysUnlocked());
	}

	@ParameterizedTest
	@CsvSource({ "8, 8, 0", "0, -5000, 25", "1000, -1000, 45", "-150, -10, 20" })
	void testProbabilitiesAreIdenticalToApproximatedPrior(double x, double z, double angle) {
		Ring ring = Ring.get(0);
		int radius = (int) Math.ceil(ring.outerRadiusPostSnapping);
		double ringArea = Math.PI * (ring.outerRadiusPostSnapping * ring.outerRadiusPostSnapping - ring.innerRadiusPostSnapping * ring.innerRadiusPostSnapping);
		double averageWeightInRing = ring.numStrongholds / ringArea;

		IPrior rayApproximatedPrior = new RayApproximatedPrior(TestUtils.createRay(x, z, angle * Math.PI / 180), 10 * Math.PI / 180, divineContext, McVersion.PRE_119);
		IPrior approximatedPrior = new ApproximatedPrior(0, 0, radius, divineContext);

		Map<Chunk, Chunk> expectedChunks = new HashMap<>();
		for (Chunk chunk : approximatedPrior.getChunks()) {
			expectedChunks.put(chunk, chunk);
		}

		double totalSquaredError = 0;
		double totalError = 0;
		int numNonZeroChunks = 0;
		int numChunks = 0;
		for (Chunk chunk : rayApproximatedPrior.getChunks()) {
			Chunk expected = expectedChunks.get(chunk);
			if (expected == null)
				continue;
			assertEquals(expected.weight, chunk.weight, averageWeightInRing * 0.05, "Maximum allowed relative error is 5%, failed for chunk: " + chunk.toString());
			numChunks++;
			double error = chunk.weight - expected.weight;
			totalError += error;
			if (expected.weight != 0) {
				numNonZeroChunks++;
				totalSquaredError += error * error;
			}
		}
		double meanError = totalError / numChunks;
		double rootMeanSquare = Math.sqrt(totalSquaredError / numNonZeroChunks);
		assertEquals(0, rootMeanSquare / averageWeightInRing, 0.001, "Relative RMS in ring exceeded maximum tolerance of 0.1%.");
		assertEquals(0, meanError, 1e-6, "Mean error exceeded maximum tolerance of 1 PPM.");
	}

	@ParameterizedTest
	@CsvSource({ "8, 8, 0", "1000, -1000, 45", "-150, -10, 20" })
	void testApproximationAccuracyFirstRing(double x, double z, double angle) {
		Ring ring = Ring.get(0);
		int radius = (int) Math.ceil(ring.outerRadiusPostSnapping);
		double ringArea = Math.PI * (ring.outerRadiusPostSnapping * ring.outerRadiusPostSnapping - ring.innerRadiusPostSnapping * ring.innerRadiusPostSnapping);
		double averageWeightInRing = ring.numStrongholds / ringArea;

		IPrior rayApproximatedPrior = new RayApproximatedPrior(TestUtils.createRay(x, z, angle * Math.PI / 180), 10 * Math.PI / 180, divineContext, McVersion.PRE_119);
		IPrior truePrior = new Prior(0, 0, radius, divineContext);

		Map<Chunk, Chunk> expectedChunks = new HashMap<>();
		for (Chunk chunk : truePrior.getChunks()) {
			expectedChunks.put(chunk, chunk);
		}

		double totalSquaredError = 0;
		double totalError = 0;
		int numNonZeroChunks = 0;
		int numChunks = 0;
		for (Chunk chunk : rayApproximatedPrior.getChunks()) {
			Chunk expected = expectedChunks.get(chunk);
			if (expected == null)
				continue;
			assertEquals(expected.weight, chunk.weight, averageWeightInRing * 0.15, "Maximum allowed relative error is 15%, failed for chunk: " + chunk.toString());
			numChunks++;
			double error = chunk.weight - expected.weight;
			totalError += error;
			if (expected.weight != 0) {
				numNonZeroChunks++;
				totalSquaredError += error * error;
			}
		}
		double meanError = totalError / numChunks;
		double rootMeanSquare = Math.sqrt(totalSquaredError / numNonZeroChunks);
		assertEquals(0, rootMeanSquare / averageWeightInRing, 0.015, "Relative RMS in ring exceeded maximum tolerance of 1.5%.");
		assertEquals(0, meanError, 1e-6, "Mean error exceeded maximum tolerance of 1 PPM.");
	}

	@ParameterizedTest
	@CsvSource({ "0, -5000, 25" })
	void testApproximationAccuracySecondRing(double x, double z, double angle) {
		Ring ring = Ring.get(1);
		int radius = (int) Math.ceil(ring.outerRadiusPostSnapping);
		double ringArea = Math.PI * (ring.outerRadiusPostSnapping * ring.outerRadiusPostSnapping - ring.innerRadiusPostSnapping * ring.innerRadiusPostSnapping);
		double averageWeightInRing = ring.numStrongholds / ringArea;

		IPrior rayApproximatedPrior = new RayApproximatedPrior(TestUtils.createRay(x, z, angle * Math.PI / 180), 10 * Math.PI / 180, divineContext, McVersion.PRE_119);
		IPrior truePrior = new Prior(0, 0, radius, divineContext);

		Map<Chunk, Chunk> expectedChunks = new HashMap<>();
		for (Chunk chunk : truePrior.getChunks()) {
			expectedChunks.put(chunk, chunk);
		}

		double totalSquaredError = 0;
		double totalError = 0;
		int numNonZeroChunks = 0;
		int numChunks = 0;
		for (Chunk chunk : rayApproximatedPrior.getChunks()) {
			Chunk expected = expectedChunks.get(chunk);
			if (expected == null)
				continue;
			assertEquals(expected.weight, chunk.weight, averageWeightInRing * 0.15, "Maximum allowed relative error is 15%, failed for chunk: " + chunk.toString());
			numChunks++;
			double error = chunk.weight - expected.weight;
			totalError += error;
			if (expected.weight != 0) {
				numNonZeroChunks++;
				totalSquaredError += error * error;
			}
		}
		double meanError = totalError / numChunks;
		double rootMeanSquare = Math.sqrt(totalSquaredError / numNonZeroChunks);
		assertEquals(0, rootMeanSquare / averageWeightInRing, 0.02, "Relative RMS in ring exceeded maximum tolerance of 2%.");
		assertEquals(0, meanError, 1e-6, "Mean error exceeded maximum tolerance of 1 PPM.");
	}

}
