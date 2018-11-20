package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.PackageRepositoryDao;
import com.kiss.kissnest.entity.BuildLog;
import com.kiss.kissnest.entity.PackageRepository;
import com.kiss.kissnest.util.ResultOutputUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import output.ResultOutput;

import java.util.List;

@Service
public class PackageRepositoryService {

    @Autowired
    private PackageRepositoryDao packageRepositoryDao;

    public Integer createPackageRepository(BuildLog buildLog) {

        PackageRepository jobRepository = new PackageRepository();
        jobRepository.setProjectId(buildLog.getProjectId());
        jobRepository.setBuildLogId(buildLog.getId());
        jobRepository.setJarName(buildLog.getJarName());
        jobRepository.setVersion(buildLog.getVersion());

        switch (buildLog.getType()) {
            case 1:
                jobRepository.setBranch(buildLog.getBranch());
                break;
            case 2:
                jobRepository.setTag(buildLog.getBranch());
        }

        jobRepository.setType(buildLog.getType());
        jobRepository.setBuildAt(buildLog.getBuildAt());
        PackageRepository exist = packageRepositoryDao.getPackageRepository(jobRepository);

        if (exist != null) {
            return packageRepositoryDao.updatePackageRepository(jobRepository);
        }

        return packageRepositoryDao.createPackageRepository(jobRepository);
    }

    public ResultOutput getPackageRepositoryBranches(Integer projectId) {

        PackageRepository jobRepository = new PackageRepository();
        jobRepository.setProjectId(projectId);
        jobRepository.setType(0);
        List<String> branches = packageRepositoryDao.getPackageRepositoryBranches(jobRepository);

        return ResultOutputUtil.success(branches);
    }

    public ResultOutput getPackageRepositoryTags(Integer projectId) {

        PackageRepository jobRepository = new PackageRepository();
        jobRepository.setProjectId(projectId);
        jobRepository.setType(1);
        List<String> tags = packageRepositoryDao.getPackageRepositoryTags(jobRepository);

        return ResultOutputUtil.success(tags);
    }
}
