package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.PackageRepository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PackageRepositoryMapper {

    Integer createPackageRepository(PackageRepository jobRepository);

    Integer updatePackageRepository(PackageRepository jobRepository);

    PackageRepository getPackageRepository(PackageRepository jobRepository);

    List<String> getPackageRepositoryBranches(PackageRepository jobRepository);

    List<String> getPackageRepositoryTags(PackageRepository jobRepository);
}