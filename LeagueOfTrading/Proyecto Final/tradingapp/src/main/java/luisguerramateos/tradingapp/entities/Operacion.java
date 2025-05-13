package luisguerramateos.tradingapp.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "operacion")
public class Operacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idactivo", nullable = false)
    private Activo activo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacion", nullable = false)
    private TipoOperacion tipoOperacion;

    @Column(nullable = false, columnDefinition = "DECIMAL(15,2)")
    private BigDecimal cantidad;

    @Column(nullable = false, columnDefinition = "DECIMAL(15,2)")
    private BigDecimal precio;

    @Column(name = "valor_total", nullable = false, columnDefinition = "DECIMAL(15,2)")
    private BigDecimal valorTotal;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean cerrada = false;

    @Column(name = "beneficio_perdida", columnDefinition = "DECIMAL(15,2) DEFAULT 0.00")
    private BigDecimal beneficioPerdida = BigDecimal.ZERO;

    public Operacion() {}

    // Getters and Setters

    public enum TipoOperacion {
        compra, venta
    }

    public Operacion(Usuario usuario, Activo activo, TipoOperacion tipoOperacion, BigDecimal cantidad, 
        BigDecimal precio, BigDecimal valorTotal, boolean cerrada, BigDecimal beneficioPerdida) {
        this.usuario = usuario;
        this.activo = activo;
        this.tipoOperacion = tipoOperacion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.valorTotal = valorTotal;
        this.cerrada = cerrada;
        this.beneficioPerdida = beneficioPerdida;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Activo getActivo() {
        return activo;
    }

    public void setActivo(Activo activo) {
        this.activo = activo;
    }

    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public boolean isCerrada() {
        return cerrada;
    }

    public void setCerrada(boolean cerrada) {
        this.cerrada = cerrada;
    }

    public BigDecimal getBeneficioPerdida() {
        return beneficioPerdida;
    }

    public void setBeneficioPerdida(BigDecimal beneficioPerdida) {
        this.beneficioPerdida = beneficioPerdida;
    }
    
    
}

