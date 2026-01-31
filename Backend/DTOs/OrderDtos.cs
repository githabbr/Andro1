namespace IncomingGoodsBackend.DTOs
{
    public class OrderDto
    {
        public int Id { get; set; }
        public string Barcode { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public string? SupplierName { get; set; }
        public DateTime OrderDate { get; set; }
        public DateTime? ArrivalDate { get; set; }
        public DateTime? CompletionDate { get; set; }
        public bool IsClosed { get; set; }
        public string Status { get; set; } = "Pending";
        public List<PhotoDto> Photos { get; set; } = new List<PhotoDto>();
    }

    public class PhotoDto
    {
        public int Id { get; set; }
        public string FileName { get; set; } = string.Empty;
        public DateTime UploadDate { get; set; }
    }

    public class PhotoUploadDto
    {
        public required string FileName { get; set; }
        public required string Base64Data { get; set; }
    }
}
