package com.api.controller;

import com.api.exceptions.CuentaNotFoundException;
import com.api.model.Cuenta;
import com.api.model.Monto;
import com.api.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {
    @Autowired
    private CuentaService service;

    @GetMapping
    public ResponseEntity<List<Cuenta>> listarcuenta(){
        List<Cuenta> cuentas = service.listAll();
        if (cuentas.isEmpty()){ // si la lista esta vacia
            return ResponseEntity.noContent().build();
        }else {
            for(Cuenta cuenta : cuentas){
                cuenta.add(linkTo(methodOn(CuentaController.class).buscarById(cuenta.getId())).withSelfRel());
                cuenta.add(linkTo(methodOn(CuentaController.class).listarcuenta()).withRel(IanaLinkRelations.COLLECTION));
            }
            return new ResponseEntity<>(cuentas, HttpStatus.OK); //retorna la lista con un code status OK
        }
    }

    /*@GetMapping
    public ResponseEntity<CollectionModel<Cuenta>> listarcuenta() {
        List<Cuenta> cuentas = service.listAll();
        if (cuentas.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            for(Cuenta cuenta : cuentas){ //agrega enlaces a cada cuenta individualmente
                cuenta.add(linkTo(methodOn(CuentaController.class).buscarById(cuenta.getId())).withSelfRel());
                cuenta.add(linkTo(methodOn(CuentaController.class).listarcuenta()).withRel(IanaLinkRelations.COLLECTION));
            }
            CollectionModel<Cuenta> modelo = CollectionModel.of(cuentas);
            modelo.add(linkTo(methodOn(CuentaController.class).listarcuenta()).withSelfRel());
            return new ResponseEntity<>(modelo, HttpStatus.OK); //aca usamos CollectionModel --> agrega un enlace para toda la colección
        }
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<Cuenta> buscarById(@PathVariable Integer id){
        try {
            Cuenta cuenta = service.findById(id);
            //pasar a static el import WebMvcLinkBuilder para ahorrar code
            cuenta.add(linkTo(methodOn(CuentaController.class).buscarById(cuenta.getId())).withSelfRel()); //withSelfReal indica que es un metodo propio/recurso
            //                             clase                  metodo

            cuenta.add(linkTo(methodOn(CuentaController.class).listarcuenta()).withRel(IanaLinkRelations.COLLECTION)); //apunta al metodo listar para eso se pone COLLECTION
            return new ResponseEntity<>(cuenta, HttpStatus.OK);
        }catch (Exception exception){
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/save")
    public ResponseEntity<Cuenta> guardarCuenta(@RequestBody Cuenta cuenta){
        Cuenta cuentaGuardar = service.guardar(cuenta);
        cuentaGuardar.add(linkTo(methodOn(CuentaController.class).buscarById(cuentaGuardar.getId())).withSelfRel());
        cuentaGuardar.add(linkTo(methodOn(CuentaController.class).listarcuenta()).withRel(IanaLinkRelations.COLLECTION));
        return ResponseEntity.created(linkTo(methodOn(CuentaController.class).buscarById(cuentaGuardar.getId())).toUri()).body(cuentaGuardar);
    }

    @PutMapping("/update")
    public ResponseEntity<Cuenta> updateCuenta(@RequestBody Cuenta cuenta){
        Cuenta cuentaGuardar = service.guardar(cuenta);
        cuentaGuardar.add(linkTo(methodOn(CuentaController.class).buscarById(cuentaGuardar.getId())).withSelfRel());
        cuentaGuardar.add(linkTo(methodOn(CuentaController.class).listarcuenta()).withRel(IanaLinkRelations.COLLECTION));
        return new ResponseEntity<>(cuentaGuardar, HttpStatus.OK);
    }

    @PatchMapping("/deposito/{id}")
    public ResponseEntity<Cuenta> despositarDinero(@PathVariable Integer id,@RequestBody Monto monto){ //puede ser float monto y ya no crear clase tbm
        Cuenta cuentaBBDD = service.depositar(monto.getMonto(),id);
        cuentaBBDD.add(linkTo(methodOn(CuentaController.class).buscarById(cuentaBBDD.getId())).withSelfRel());
        cuentaBBDD.add(linkTo(methodOn(CuentaController.class).despositarDinero(cuentaBBDD.getId(),null)).withRel("depositooooo")); //withrel establece el nombre del enlace
        //                             parametros de el metodo despositarDinero(@PathVariable Integer id,@RequestBody Monto monto
        return new ResponseEntity<>(cuentaBBDD,HttpStatus.OK);
    }

    @PatchMapping("/{id}/retiro")
    public ResponseEntity<Cuenta> retirarDinero(@PathVariable Integer id,@RequestBody Monto monto){
        Cuenta cuentaBBDD = service.retirar(monto.getMonto(),id);

        cuentaBBDD.add(linkTo(methodOn(CuentaController.class).buscarById(cuentaBBDD.getId())).withSelfRel());
        cuentaBBDD.add(linkTo(methodOn(CuentaController.class).despositarDinero(cuentaBBDD.getId(),null)).withRel("depositos"));
        cuentaBBDD.add(linkTo(methodOn(CuentaController.class).retirarDinero(cuentaBBDD.getId(),null)).withRel("retiros"));

        return new ResponseEntity<>(cuentaBBDD,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> eliminarCuenta(@PathVariable Integer id){
        try {
            service.delete(id);
            return  ResponseEntity.noContent().build();
        } catch (CuentaNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
