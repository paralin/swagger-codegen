package io.swagger.petstore.test;

import io.swagger.client.ApiClient;
import io.swagger.client.api.*;
import io.swagger.client.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.*;

import retrofit.Response;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import static org.junit.Assert.*;

public class PetApiTest {
    PetApi api = null;

    @Before
    public void setup() {
        api = new ApiClient().createService(PetApi.class);
    }

    @Test
    public void testCreateAndGetPet() throws Exception {
        Pet pet = createRandomPet();
        Response<Void> rp2 = api.addPet(pet).execute();
        
        Response<Pet> rp = api.getPetById(pet.getId()).execute();
        Pet fetched = rp.body();
        assertNotNull(fetched);
        assertEquals(pet.getId(), fetched.getId());
        assertNotNull(fetched.getCategory());
        assertEquals(fetched.getCategory().getName(), pet.getCategory().getName());
    }

    @Test
    public void testUpdatePet() throws Exception {
        Pet pet = createRandomPet();
        pet.setName("programmer");

        api.updatePet(pet).execute();

        Pet fetched = api.getPetById(pet.getId()).execute().body();
        assertNotNull(fetched);
        assertEquals(pet.getId(), fetched.getId());
        assertNotNull(fetched.getCategory());
        assertEquals(fetched.getCategory().getName(), pet.getCategory().getName());
    }

    @Test
    public void testFindPetsByStatus() throws Exception {
        Pet pet = createRandomPet();
        pet.setName("programmer");
        pet.setStatus(Pet.StatusEnum.AVAILABLE);

        api.updatePet(pet).execute();

        List<Pet> pets = api.findPetsByStatus(Arrays.asList(new String[]{"available"})).execute().body();
        assertNotNull(pets);

        boolean found = false;
        for (Pet fetched : pets) {
            if (fetched.getId().equals(pet.getId())) {
                found = true;
                break;
            }
        }

        assertTrue(found);
    }

    @Test
    public void testFindPetsByTags() throws Exception {
        Pet pet = createRandomPet();
        pet.setName("monster");
        pet.setStatus(Pet.StatusEnum.AVAILABLE);

        List<Tag> tags = new ArrayList<Tag>();
        Tag tag1 = new Tag();
        tag1.setName("friendly");
        tags.add(tag1);
        pet.setTags(tags);

        api.updatePet(pet).execute();

        List<Pet> pets = api.findPetsByTags(Arrays.asList(new String[]{"friendly"})).execute().body();
        assertNotNull(pets);

        boolean found = false;
        for (Pet fetched : pets) {
            if (fetched.getId().equals(pet.getId())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testUpdatePetWithForm() throws Exception {
        Pet pet = createRandomPet();
        pet.setName("frank");
        api.addPet(pet).execute();

        Pet fetched = api.getPetById(pet.getId()).execute().body();

        api.updatePetWithForm(String.valueOf(fetched.getId()), "furt", null).execute();
        Pet updated = api.getPetById(fetched.getId()).execute().body();

        assertEquals(updated.getName(), "furt");
    }

    @Test
    public void testDeletePet() throws Exception {
        Pet pet = createRandomPet();
        api.addPet(pet).execute();

        Pet fetched = api.getPetById(pet.getId()).execute().body();
        api.deletePet(fetched.getId(), null).execute();

        assertFalse(api.getPetById(fetched.getId()).execute().isSuccess());
    }

    @Test
    public void testUploadFile() throws Exception {
        Pet pet = createRandomPet();
        api.addPet(pet).execute();

        File file = new File("hello.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("Hello world!");
        writer.close();

        api.uploadFile(pet.getId(), "a test file", RequestBody.create(MediaType.parse("text/plain"), file)).execute();
    }

    private Pet createRandomPet() {
        Pet pet = new Pet();
        pet.setId(System.currentTimeMillis());
        pet.setName("gorilla");

        Category category = new Category();
        category.setName("really-happy");

        pet.setCategory(category);
        pet.setStatus(Pet.StatusEnum.AVAILABLE);
        List<String> photos = Arrays.asList(new String[]{"http://foo.bar.com/1", "http://foo.bar.com/2"});
        pet.setPhotoUrls(photos);

        return pet;
    }
}