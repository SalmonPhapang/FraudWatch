package com.fraudwatch.fraudruleengine.mapper;

import com.fraudwatch.fraudruleengine.dto.FraudRuleConditionDTO;
import com.fraudwatch.fraudruleengine.dto.FraudRuleDTO;
import com.fraudwatch.fraudruleengine.entity.FraudRule;
import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FraudRuleMapper {

    FraudRuleDTO toDTO(FraudRule fraudRule);

    List<FraudRuleDTO> toDTOList(List<FraudRule> fraudRules);

    @Mapping(target = "conditions", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FraudRule toEntity(FraudRuleDTO dto);

    FraudRuleConditionDTO toConditionDTO(FraudRuleCondition condition);

    List<FraudRuleConditionDTO> toConditionDTOList(List<FraudRuleCondition> conditions);

    @Mapping(target = "fraudRule", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    FraudRuleCondition toConditionEntity(FraudRuleConditionDTO dto);
}
