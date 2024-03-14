import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import java.util.List;


public class SpaceXIntegrationTest {
    private static final String CAPSULES_URL = "https://api.spacexdata.com/v3/capsules";

    private static final String UPCOMING_CAPSULES_URL = "https://api.spacexdata.com/v3/capsules/upcoming";

    @Test

    public void testIntegration() {
        // Send request to get all capsule serials
        Response responseCapsules = RestAssured.get(CAPSULES_URL);
        Assert.assertEquals(responseCapsules.getStatusCode(), 200);

        // Get all capsule serials from response
        List<String> allCapsuleSerials = responseCapsules.jsonPath().getList("capsule_serial");
        Assert.assertFalse(allCapsuleSerials.isEmpty(), "No capsule serials found in response");

        // Print response body for debugging purposes
        String responseBody = responseCapsules.getBody().asString();
        System.out.println("Response Body: " + responseBody);

        // Send request to get upcoming capsule serials
        Response responseUpcomingCapsules = RestAssured.get(UPCOMING_CAPSULES_URL);
        Assert.assertEquals(responseUpcomingCapsules.getStatusCode(), 200);

        // Get all upcoming capsule serials from response
        List<String> upcomingCapsuleSerials = responseUpcomingCapsules.jsonPath().getList("capsule_serial");
        Assert.assertFalse(upcomingCapsuleSerials.isEmpty(), "No upcoming capsule serials found in response");

        // Find common ids between all and upcoming capsule serials
        allCapsuleSerials.retainAll(upcomingCapsuleSerials);
        Assert.assertFalse(allCapsuleSerials.isEmpty(), "No common ids found between all and upcoming capsules");

        // Make request call with common id and verify original_launch is null
        String commonId = allCapsuleSerials.get(0);
        String capsuleWithIdUrl = CAPSULES_URL + "/" + commonId;
        Response responseCommonId = RestAssured.get(capsuleWithIdUrl);
        Assert.assertEquals(responseCommonId.getStatusCode(), 200);
        Assert.assertNull(responseCommonId.jsonPath().getString("original_launch"), "Original launch is not null for the common id");

        // Get capsule names with flight parameter set as 10 from response
        List<String> capsulesWithFlight10 = responseCapsules.jsonPath().getList("findAll { it.missions.find { mission -> mission.flight == 10 } != null }.capsule_serial");
        Assert.assertFalse(capsulesWithFlight10.isEmpty(), "No capsules found with flight parameter set as 10");



        // Send request to get capsule names with flight parameter set as 10
       // Response responseCapsulesWithFlight10 = RestAssured.get(CAPSULES_URL);
       // Assert.assertEquals(responseCapsulesWithFlight10.getStatusCode(), 200);

        // Get capsule names with flight parameter set as 10 from response
       // List<String> capsulesWithFlight10 = responseCapsulesWithFlight10.jsonPath().getList("findAll { it.flight == 10 }.name");
       // Assert.assertFalse(capsulesWithFlight10.isEmpty(), "No capsules found with flight parameter set as 10");
    }
}
