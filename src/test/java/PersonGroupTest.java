import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.mikeski.domain.Group;
import net.mikeski.domain.Person;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by mike on 1/13/17.
 */
public class PersonGroupTest {

    @Test
    public void testSetGroup(){
        Person person = new Person(1l, "Mike");
        Group group = new Group( 2l, "First Group");

        assertNotNull(person);
        assertNotNull(group);

        person.addGroup(group);
        assertEquals(1, person.getGroups().size());
        assertEquals(1, group.getPeople().size());
    }

    @Test
    public void testJsonSerializationCircularReference() throws JsonProcessingException {
        Person person = new Person(1l, "Mike");
        Group group = new Group( 2l, "First Group");
        ObjectMapper mapper = new ObjectMapper();

        person.addGroup(group);

        String personJson = mapper.writeValueAsString(person);
        System.out.println("JSON for Person with 1 group: ");
        System.out.println(personJson);
    }
}
