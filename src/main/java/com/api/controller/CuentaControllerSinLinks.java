package com.api.controller;

import com.api.model.Cuenta;
import com.api.model.Monto;
import com.api.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas/nolink")
public class CuentaControllerSinLinks {
    @Autowired
    private CuentaService service;

    @GetMapping
    public ResponseEntity<List<Cuenta>> listarcuenta(){
        List<Cuenta> cuentas = service.listAll();
        if (cuentas.isEmpty()){ // si la lista esta vacia
            return ResponseEntity.noContent().build();
        }else {
            return new ResponseEntity<>(cuentas, HttpStatus.OK); //retorna la lista con un code status OK
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cuenta> buscarById(@PathVariable Integer id){
        try {
            Cuenta cuenta = service.findById(id);
            return new ResponseEntity<>(cuenta, HttpStatus.OK);
        }catch (Exception exception){
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/save")
    public ResponseEntity<Cuenta> guardarCuenta(@RequestBody Cuenta cuenta){
        Cuenta cuentaGuardar = service.guardar(cuenta);
        return new ResponseEntity<>(cuentaGuardar, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Cuenta> updateCuenta(@RequestBody Cuenta cuenta){
        Cuenta cuentaGuardar = service.guardar(cuenta);
        return new ResponseEntity<>(cuentaGuardar, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<Cuenta> despositarDinero(@PathVariable Integer id,@RequestBody Monto monto){ //puede ser float monto y ya no crear clase tbm
        Cuenta cuentaBBDD = service.depositar(monto.getMonto(),id);
        return new ResponseEntity<>(cuentaBBDD,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> eliminarCuenta(@PathVariable Integer id){
        try {
            service.delete(id);
            return  ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
