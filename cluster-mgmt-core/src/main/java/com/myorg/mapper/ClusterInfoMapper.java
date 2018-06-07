package com.myorg.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.myorg.dto.AutoScaleDTO;
import com.myorg.dto.AwsAttributesDTO;
import com.myorg.dto.ClusterInfoDTO;
import com.myorg.entity.ClusterInfo;
import com.myorg.databricks.cluster.AutoScale;
import com.myorg.databricks.cluster.AwsAttributes;

@Mapper(componentModel = "spring")
public interface ClusterInfoMapper {
  ClusterInfoMapper MAPPER = Mappers.getMapper(ClusterInfoMapper.class);

  @Mappings({@Mapping(target = "driver", ignore = false),
//      @Mapping(target = "executors", ignore = true),
      @Mapping(target = "defaultTags", ignore = false),
      @Mapping(target = "clusterLogStatus", ignore = true),
//      @Mapping(target = "terminationReason", ignore = true),
      @Mapping(target = "awsAttributes", ignore = false),
      @Mapping(target = "awsAttributes.accessKey", ignore = false),
      @Mapping(target = "sparkConf", ignore = true),
      // @Mapping(target = "customTags", ignore = true),
      @Mapping(target = "clusterLogConf", ignore = true),
      @Mapping(target = "sparkEnvironmentVariables", ignore = true)

  })
  ClusterInfoDTO mapClusterInfoToClusterInfoDTO(ClusterInfo clusterInfo);

  @Mappings({
//    @Mapping(target = "executors", ignore = true),
    @Mapping(target = "clusterLogStatus", ignore = true),
    @Mapping(target = "terminationReason", ignore = true),
    @Mapping(target = "awsAttributes", ignore = false),
    // @Mapping(target = "customTags", ignore = true),
    @Mapping(target = "clusterLogConf", ignore = true)
  })
  ClusterInfo mapClusterDTOToClusterInfo(ClusterInfoDTO clusterInfoDTO);


  AwsAttributesDTO getAwsAttributesDTO(AwsAttributes awsAttributes);
  AutoScaleDTO getAutoScaleDTO(AutoScale autoScale);

}
