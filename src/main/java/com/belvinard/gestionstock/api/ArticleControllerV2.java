@RestController
@RequestMapping("/api/v2/articles")
@RequiredArgsConstructor
@Validated
@Tag(name = "Articles V2", description = "API moderne de gestion des articles")
public class ArticleControllerV2 {
    
    private final ArticleService articleService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STOCK_MANAGER')")
    @Operation(summary = "Créer un article")
    public ResponseEntity<ApiResponse<ArticleDTO>> createArticle(
            @Valid @RequestBody CreateArticleRequest request) {
        
        ArticleDTO article = articleService.createArticle(request);
        
        return ResponseEntity
            .status(CREATED)
            .body(ApiResponse.success("Article créé avec succès", article));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STOCK_MANAGER', 'SALES_MANAGER')")
    @Operation(summary = "Lister les articles avec pagination")
    public ResponseEntity<PagedResponse<ArticleDTO>> getArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId) {
        
        PagedResponse<ArticleDTO> articles = articleService.getArticles(
            PageRequest.of(page, size), search, categoryId);
            
        return ResponseEntity.ok(articles);
    }
    
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'STOCK_MANAGER')")
    @Operation(summary = "Ajuster le stock")
    public ResponseEntity<ApiResponse<Void>> adjustStock(
            @PathVariable Long id,
            @Valid @RequestBody StockAdjustmentRequest request) {
        
        articleService.adjustStock(id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Stock ajusté"));
    }
}