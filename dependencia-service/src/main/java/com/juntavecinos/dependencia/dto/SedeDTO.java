package com.juntavecinos.dependencia.dto;

import com.juntavecinos.dependencia.model.Sede;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SedeDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre es demasiado largo") String nombre,
        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 200, message = "La dirección es demasiado larga") String direccion,
        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 1000, message = "La descripción es demasiado larga") String descripcion,
        @NotBlank(message = "El reglamento es obligatorio")
        @Size(max = 1000, message = "El reglamento es demasiado largo") String reglamento,
        @NotBlank(message = "Las instrucciones de pago son obligatorias")
        @Size(max = 1000, message = "Las instrucciones de pago son demasiado largas") String instruccionesPago) {

    public static SedeDTO fromModel(Sede s) {
        return new SedeDTO(s.getNombre(), s.getDireccion(), s.getDescripcion(), s.getReglamento(),
                s.getInstruccionesPago());
    }
}
