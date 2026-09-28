package com.example.transit.service;

import com.example.transit.entity.Bus;
import com.example.transit.exception.InvalidOperationException;
import com.example.transit.exception.ResourceNotFoundException;
import com.example.transit.repository.BusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;

    @Transactional
    public Bus createBus(Bus bus) {
        if (busRepository.existsByRegistrationNumber(bus.getRegistrationNumber())) {
            throw new InvalidOperationException(
                    "A bus with registration number " + bus.getRegistrationNumber() + " already exists");
        }
        bus.setId(null);
        return busRepository.save(bus);
    }

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Bus getBusById(Long id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id " + id));
    }

    @Transactional
    public Bus updateBus(Long id, Bus updated) {
        Bus bus = getBusById(id);
        bus.setRegistrationNumber(updated.getRegistrationNumber());
        bus.setModel(updated.getModel());
        bus.setCapacity(updated.getCapacity());
        bus.setStatus(updated.getStatus());
        return busRepository.save(bus);
    }

    @Transactional
    public void deleteBus(Long id) {
        Bus bus = getBusById(id);
        busRepository.delete(bus);
    }
}
