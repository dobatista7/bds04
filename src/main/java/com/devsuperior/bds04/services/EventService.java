package com.devsuperior.bds04.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds04.dto.CityDTO;
import com.devsuperior.bds04.dto.EventDTO;
import com.devsuperior.bds04.entities.City;
import com.devsuperior.bds04.entities.Event;
import com.devsuperior.bds04.repositories.CityRepository;
import com.devsuperior.bds04.repositories.EventRepository;
import com.devsuperior.bds04.services.exceptions.ResourceNotFoundException;

@Service
public class EventService {
	
	@Autowired
	private EventRepository repository;
	
	@Autowired
	private CityRepository cityRepository;
	
	public Page<EventDTO> findAll(Pageable pageable){
		Page<Event> page = repository.findAll(pageable);
		return page.map(x -> new EventDTO(x));
		
	}
	
    @Transactional
	public EventDTO findById(Long id) {
		Optional <Event> obj = repository.findById(id);
		Event entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new EventDTO(entity);
	}

    
    @Transactional
	public EventDTO insert(EventDTO dto) {
		Event entity = new Event();
		entity.setName(dto.getName());
		entity.setDate(dto.getDate());
		entity.setUrl(dto.getUrl());
		entity.setCity(new City(dto.getCityId(), null));
		entity = repository.save(entity);
		return new EventDTO(entity);
	}
    
    
    @Transactional
	public EventDTO update(Long id, EventDTO dto) {
    	try {
        	Event entity = repository.getOne(id);
    		copyDtoToEntity(dto, entity);
        	entity = repository.save(entity);
    		return new EventDTO(entity);
        	}
        	catch(EntityNotFoundException  e ) {
        		
        		throw new ResourceNotFoundException("Id not found "+ id);    		
        		
        	}   
	}

	private void copyDtoToEntity(EventDTO dto, Event entity) {
		entity.setName(dto.getName());
		entity.setDate(dto.getDate());
		entity.setUrl(dto.getUrl());
		entity.setCity(new City(dto.getCityId(), null));
						
		entity.getCities().clear();
		for(CityDTO citDto : dto.getCities()) {
			City city = cityRepository.getOne(citDto.getId());
			entity.getCities().add(city);
			
		}

	}
}
