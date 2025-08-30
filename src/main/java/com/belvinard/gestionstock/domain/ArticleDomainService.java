import com.belvinard.gestionstock.models.Article;

import static com.belvinard.gestionstock.models.TypeMvtStk.*;

@Service
@Transactional
@RequiredArgsConstructor
@Builder
@Slf4j
public class ArticleDomainService {
    
    private final ArticleRepository articleRepository;
    private final StockEventPublisher stockEventPublisher;
    private final ArticleValidator articleValidator;
    
    public Article creerArticle(CreateArticleCommand command) {
        log.info("Création article: {}", command.getCodeArticle());
        
        articleValidator.validerCreation(command);
        
        Article article = Article.builder()
            .codeArticle(command.getCodeArticle())
            .designation(command.getDesignation())
            .prixUnitaireHt(command.getPrixUnitaireHt())
            .tauxTva(command.getTauxTva())
            .quantiteEnStock(0L)
            .build();
            
        Article saved = articleRepository.save(article);
        
        stockEventPublisher.publishArticleCreated(saved);
        
        return saved;
    }
    
    @EventListener
    public void handleStockMovement(StockMovementEvent event) {
        Article article = articleRepository.findById(event.getArticleId())
            .orElseThrow(() -> new ArticleNotFoundException(event.getArticleId()));
            
        switch (event.getType()) {
            case ENTREE -> article.ajouterStock(event.getQuantite());
            case SORTIE -> article.retirerStock(event.getQuantite());
            case RESERVATION -> article.reserverStock(event.getQuantite());
        }
        
        articleRepository.save(article);
    }
}