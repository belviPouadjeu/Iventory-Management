@Entity
@Table(name = "articles", indexes = {
    @Index(name = "idx_article_code_entreprise", columnList = "codeArticle, entreprise_id"),
    @Index(name = "idx_article_category", columnList = "category_id"),
    @Index(name = "idx_article_stock", columnList = "quantiteEnStock")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleModern extends AbstractEntity {
    
    @Column(nullable = false, length = 50)
    private String codeArticle;
    
    @Column(nullable = false, length = 200)
    private String designation;
    
    @Builder.Default
    private Long quantiteEnStock = 0L;
    
    @Builder.Default
    private Long quantiteReservee = 0L;
    
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal prixUnitaireHt;
    
    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal tauxTva;
    
    @Formula("prix_unitaire_ht * (1 + taux_tva / 100)")
    private BigDecimal prixUnitaireTtc;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;
    
    // Méthodes métier
    public boolean isStockSuffisant(Long quantiteDemandee) {
        return getStockDisponible() >= quantiteDemandee;
    }
    
    public Long getStockDisponible() {
        return quantiteEnStock - quantiteReservee;
    }
}