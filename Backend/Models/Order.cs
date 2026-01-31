namespace IncomingGoodsBackend.Models
{
    public class Order
    {
        public int Id { get; set; }
        public required string Barcode { get; set; }
        public required string Description { get; set; }
        public string? SupplierName { get; set; }
        public DateTime OrderDate { get; set; }
        public DateTime? ArrivalDate { get; set; }
        public DateTime? CompletionDate { get; set; }
        public bool IsClosed { get; set; }
        public string Status { get; set; } = "Pending";
        public List<OrderPhoto> Photos { get; set; } = new List<OrderPhoto>();
    }
}
