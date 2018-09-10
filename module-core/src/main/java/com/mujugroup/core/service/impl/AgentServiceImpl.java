package com.mujugroup.core.service.impl;

import com.mujugroup.core.mapper.AgentMapper;
import com.mujugroup.core.model.Agent;
import com.mujugroup.core.objeck.vo.SelectVO;
import com.mujugroup.core.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author leolaurel
 */
@Service("agentService")
public class AgentServiceImpl implements AgentService {


    private final AgentMapper agentMapper;

    @Autowired
    public AgentServiceImpl(AgentMapper agentMapper) {
        this.agentMapper = agentMapper;
    }


    @Override
    public List<SelectVO> getAgentList() {
        return agentMapper.getAgentList();
    }


}