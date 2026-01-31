namespace IncomingGoodsBackend.Models
{
    public class OrderPhoto
    {
        public int Id { get; set; }
        public int OrderId { get; set; }
        public required string FileName { get; set; }
        public required string FilePath { get; set; }
        public DateTime UploadDate { get; set; }
        public Order? Order { get; set; }
    }
}
