package com.myorg.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.myorg.databricks.CreateClusterRequest;
import com.myorg.dto.ClusterInfoDTO;

//@Mapper(componentModel = "spring")
public interface DatabricksMapper {
  DatabricksMapper MAPPER = Mappers.getMapper(DatabricksMapper.class);

  // @Mappings({@Mapping(target = "applicationPort", source = "port")})
  // @Mapping(source = "platform", ignore = true)})
  CreateClusterRequest mapClusterInfoToCreateClusterRequest(ClusterInfoDTO clusterInfoDTO);
}
