package com.fraudwatch.fraudruleengine.service;

import com.fraudwatch.fraudruleengine.dto.FraudRuleConditionDTO;
import com.fraudwatch.fraudruleengine.dto.FraudRuleDTO;
import com.fraudwatch.fraudruleengine.entity.FraudRule;
import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;
import com.fraudwatch.fraudruleengine.mapper.FraudRuleMapper;
import com.fraudwatch.fraudruleengine.repository.FraudRuleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FraudRuleService {

    private final FraudRuleRepository fraudRuleRepository;
    private final FraudRuleMapper fraudRuleMapper;

    public FraudRuleService(FraudRuleRepository fraudRuleRepository, FraudRuleMapper fraudRuleMapper) {
        this.fraudRuleRepository = fraudRuleRepository;
        this.fraudRuleMapper = fraudRuleMapper;
    }

    @Cacheable(value = "fraudRules")
    public Page<FraudRuleDTO> getAllRules(Pageable pageable) {
        return fraudRuleRepository.findAll(pageable).map(fraudRuleMapper::toDTO);
    }

    public FraudRuleDTO getRuleById(Long id) {
        return fraudRuleRepository.findById(id)
                .map(fraudRuleMapper::toDTO)
                .orElse(null);
    }

    @Transactional
    @CacheEvict(value = "fraudRules", allEntries = true)
    public FraudRuleDTO createRule(FraudRuleDTO dto) {
        FraudRule rule = fraudRuleMapper.toEntity(dto);
        final FraudRule finalRule = rule;
        if (dto.getConditions() != null) {
            List<FraudRuleCondition> conditions = dto.getConditions().stream()
                    .map(fraudRuleMapper::toConditionEntity)
                    .peek(c -> c.setFraudRule(finalRule))
                    .toList();
            rule.setConditions(new ArrayList<>(conditions));
        }
        rule = fraudRuleRepository.save(rule);
        return fraudRuleMapper.toDTO(rule);
    }

    @Transactional
    @CacheEvict(value = "fraudRules", allEntries = true)
    public FraudRuleDTO updateRule(Long id, FraudRuleDTO dto) {
        FraudRule existing = fraudRuleRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setEnabled(dto.getEnabled());
        existing.setPriority(dto.getPriority());
        existing.setSeverity(dto.getSeverity());
        existing.setWeight(dto.getWeight());
        existing.setAction(dto.getAction());
        existing.setRuleType(dto.getRuleType());
        
        final FraudRule finalExisting = existing;
        if (dto.getConditions() != null) {
            existing.getConditions().clear();
            List<FraudRuleCondition> conditions = dto.getConditions().stream()
                    .map(fraudRuleMapper::toConditionEntity)
                    .peek(c -> c.setFraudRule(finalExisting))
                    .toList();
            existing.getConditions().addAll(conditions);
        }
        
        existing = fraudRuleRepository.save(existing);
        return fraudRuleMapper.toDTO(existing);
    }

    @Transactional
    @CacheEvict(value = "fraudRules", allEntries = true)
    public void deleteRule(Long id) {
        fraudRuleRepository.deleteById(id);
    }
}
