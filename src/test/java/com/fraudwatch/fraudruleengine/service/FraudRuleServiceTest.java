
package com.fraudwatch.fraudruleengine.service;

import com.fraudwatch.fraudruleengine.dto.FraudRuleDTO;
import com.fraudwatch.fraudruleengine.entity.FraudRule;
import com.fraudwatch.fraudruleengine.mapper.FraudRuleMapper;
import com.fraudwatch.fraudruleengine.repository.FraudRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudRuleServiceTest {

    @Mock
    private FraudRuleRepository fraudRuleRepository;

    @Mock
    private FraudRuleMapper fraudRuleMapper;

    @InjectMocks
    private FraudRuleService fraudRuleService;

    @Test
    void testGetAllRules_ReturnsPagedRules() {
        Pageable pageable = PageRequest.of(0, 10);
        FraudRule rule = new FraudRule();
        FraudRuleDTO dto = new FraudRuleDTO();
        Page<FraudRule> rulePage = new PageImpl<>(List.of(rule));

        when(fraudRuleRepository.findAll(pageable)).thenReturn(rulePage);
        when(fraudRuleMapper.toDTO(rule)).thenReturn(dto);

        Page<FraudRuleDTO> result = fraudRuleService.getAllRules(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetRuleById_WhenFound_ReturnsDTO() {
        Long id = 1L;
        FraudRule rule = new FraudRule();
        FraudRuleDTO dto = new FraudRuleDTO();

        when(fraudRuleRepository.findById(id)).thenReturn(Optional.of(rule));
        when(fraudRuleMapper.toDTO(rule)).thenReturn(dto);

        FraudRuleDTO result = fraudRuleService.getRuleById(id);

        assertNotNull(result);
    }

    @Test
    void testCreateRule_SavesAndReturnsDTO() {
        FraudRuleDTO dto = new FraudRuleDTO();
        FraudRule rule = new FraudRule();

        when(fraudRuleMapper.toEntity(dto)).thenReturn(rule);
        when(fraudRuleRepository.save(any())).thenReturn(rule);
        when(fraudRuleMapper.toDTO(rule)).thenReturn(dto);

        FraudRuleDTO result = fraudRuleService.createRule(dto);

        assertNotNull(result);
        verify(fraudRuleRepository, times(1)).save(any());
    }

    @Test
    void testUpdateRule_WhenExists_UpdatesAndReturnsDTO() {
        Long id = 1L;
        FraudRuleDTO dto = new FraudRuleDTO();
        dto.setName("Updated Rule");
        FraudRule existing = new FraudRule();

        when(fraudRuleRepository.findById(id)).thenReturn(Optional.of(existing));
        when(fraudRuleRepository.save(any())).thenReturn(existing);
        when(fraudRuleMapper.toDTO(existing)).thenReturn(dto);

        FraudRuleDTO result = fraudRuleService.updateRule(id, dto);

        assertNotNull(result);
    }

    @Test
    void testDeleteRule_DeletesById() {
        Long id = 1L;

        fraudRuleService.deleteRule(id);

        verify(fraudRuleRepository, times(1)).deleteById(id);
    }
}
