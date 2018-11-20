package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.PackageRepository;

import java.util.List;

public interface PackageRepositoryDao {

    Integer createPackageRepository(PackageRepository packageRepository);

    Integer updatePackageRepository(PackageRepository packageRepository);

    PackageRepository getPackageRepository(PackageRepository packageRepository);

    List<String> getPackageRepositoryBranches(PackageRepository packageRepository);

    List<String> getPackageRepositoryTags(PackageRepository packageRepository);
}
