package pl.dopierala.allegroreporeaderapi.service;

import pl.dopierala.allegroreporeaderapi.model.Repository;

import java.util.List;

public interface IRepoService {
    List<Repository> findRepositoriesByUserName(String userName, int userTimeZoneOffset);
}
