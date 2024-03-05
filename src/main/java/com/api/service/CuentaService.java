package com.api.service;

import com.api.exceptions.CuentaNotFoundException;
import com.api.model.Cuenta;
import com.api.repository.CuentaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CuentaService {
    @Autowired
    private CuentaRepository repository;

    public List<Cuenta> listAll(){
        return repository.findAll();
    }
    public Cuenta findById(Integer id){
        return repository.findById(id).get();
    }
    public Cuenta guardar(Cuenta cuenta){
        return repository.save(cuenta);
    }
    public void delete(Integer id) throws CuentaNotFoundException { //uso de la clase exception creada
        if (!repository.existsById(id)){
            throw new CuentaNotFoundException("Cuenta con el " + id+" no encontrada");
        }
        repository.deleteById(id);
    }
    public Cuenta depositar(float monto, Integer id){
        repository.actualizarMonto(monto,id);
        return repository.findById(id).get();
    }

    public Cuenta retirar(float monto, Integer id){
        repository.actualizarMonto(-monto,id);
        return repository.findById(id).get();
    }
}
