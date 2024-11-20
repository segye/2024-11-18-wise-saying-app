package service;

import entity.WiseSaying;
import repository.WiseSayingRepository;

import java.io.IOException;
import java.util.List;

public class WiseSayingService {
    private static final WiseSayingRepository repository = new WiseSayingRepository();

    public WiseSaying findById(int id) {
        return repository.findById(id);
    }

    public int add(String message, String author) {
        try{
            int id = repository.add(message, author);
            repository.saveLastId();
            return id;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<WiseSaying> findAll() {
        return repository.findAll();
    }

    public boolean delete(int id) {
        return repository.deleteById(id);
    }

    public boolean update(int id, String updateMessage, String updateAuthor) throws IOException {
        WiseSaying wiseSaying = repository.findById(id);
        if(wiseSaying == null){
            return false;
        }
        repository.update(id, updateMessage, updateAuthor);
        return true;
    }

    public void build() throws IOException {
        repository.fileBuild();
    }

    public int getId() {
        return repository.getId();
    }

}
