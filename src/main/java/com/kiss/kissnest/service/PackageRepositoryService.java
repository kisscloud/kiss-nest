package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.PackageRepositoryDao;
import com.kiss.kissnest.entity.BuildLog;
import com.kiss.kissnest.entity.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class PackageRepositoryService {

    @Autowired
    private PackageRepositoryDao packageRepositoryDao;

    public Integer createPackageRepository(BuildLog buildLog) {

        PackageRepository packageRepository = new PackageRepository();
        packageRepository.setProjectId(buildLog.getProjectId());
        packageRepository.setBuildLogId(buildLog.getId());
        packageRepository.setJarName(buildLog.getJarName());
        packageRepository.setVersion(buildLog.getVersion());
        packageRepository.setTarName(buildLog.getTarName());

        switch (buildLog.getType()) {
            case 1:
                packageRepository.setBranch(buildLog.getBranch());
                break;
            case 2:
                packageRepository.setTag(buildLog.getBranch());
        }

        packageRepository.setType(buildLog.getType());
        packageRepository.setBuildAt(buildLog.getBuildAt());
        PackageRepository exist = packageRepositoryDao.getPackageRepository(packageRepository);

        if (exist != null) {
            packageRepository.setId(exist.getId());
            return packageRepositoryDao.updatePackageRepository(packageRepository);
        }

        return packageRepositoryDao.createPackageRepository(packageRepository);
    }

    public List<String> getPackageRepositoryBranches(Integer projectId) {

        PackageRepository jobRepository = new PackageRepository();
        jobRepository.setProjectId(projectId);
        jobRepository.setType(0);
        List<String> branches = packageRepositoryDao.getPackageRepositoryBranches(jobRepository);

        return branches;
    }

    public List<String> getPackageRepositoryTags(Integer projectId) {

        PackageRepository jobRepository = new PackageRepository();
        jobRepository.setProjectId(projectId);
        jobRepository.setType(1);
        List<String> tags = packageRepositoryDao.getPackageRepositoryTags(jobRepository);

        return tags;
    }
}
