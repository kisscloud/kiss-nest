package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.PackageRepositoryDao;
import com.kiss.kissnest.entity.PackageRepository;
import com.kiss.kissnest.mapper.PackageRepositoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PackageRepositoryDaoImpl implements PackageRepositoryDao {

    @Autowired
    private PackageRepositoryMapper packageRepositoryMapper;

    @Override
    public Integer createPackageRepository(PackageRepository packageRepository) {

        return packageRepositoryMapper.createPackageRepository(packageRepository);
    }

    @Override
    public Integer updatePackageRepository(PackageRepository packageRepository) {

        return packageRepositoryMapper.updatePackageRepository(packageRepository);
    }

    @Override
    public PackageRepository getPackageRepository(PackageRepository packageRepository) {

        return packageRepositoryMapper.getPackageRepository(packageRepository);
    }

    @Override
    public List<String> getPackageRepositoryBranches(PackageRepository packageRepository) {

        return packageRepositoryMapper.getPackageRepositoryBranches(packageRepository);
    }

    @Override
    public List<String> getPackageRepositoryTags(PackageRepository packageRepository) {

        return packageRepositoryMapper.getPackageRepositoryTags(packageRepository);
    }
}
