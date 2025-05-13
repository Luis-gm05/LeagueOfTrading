package luisguerramateos.tradingapp.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "contra_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, columnDefinition = "DECIMAL(15,2) DEFAULT 100000")
    private BigDecimal saldo = BigDecimal.valueOf(100000);

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT '500'")
    private String puntuacion = "500";

    public Usuario(String nombre, String email, String passwordHash, BigDecimal saldo, String puntuacion) {
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.saldo = saldo;
        this.puntuacion = puntuacion;
    }
    public Usuario() {
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(String puntuacion) {
        this.puntuacion = puntuacion;
    }
    
    

    // Getters and Setters
}

