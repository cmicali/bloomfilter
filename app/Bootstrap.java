import models.Post;
import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

/**
 * Author: chrismicali
 */
@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() {
        // Check if the database is empty
        if(Post.count() == 0) {
//            Fixtures.loadModels("initial-data.yml");
//            Fixtures.loadModels("test-data.yml");
        }
    }

}