using Microsoft.EntityFrameworkCore;
using IncomingGoodsBackend.Models;

namespace IncomingGoodsBackend.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
        {
        }

        public DbSet<Order> Orders { get; set; }
        public DbSet<OrderPhoto> OrderPhotos { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<Order>()
                .HasIndex(o => o.Barcode)
                .IsUnique();

            modelBuilder.Entity<Order>()
                .HasMany(o => o.Photos)
                .WithOne(p => p.Order)
                .HasForeignKey(p => p.OrderId)
                .OnDelete(DeleteBehavior.Cascade);

            // Seed some sample data with fixed dates
            var baseDate = new DateTime(2026, 1, 26, 10, 0, 0, DateTimeKind.Utc);
            
            modelBuilder.Entity<Order>().HasData(
                new Order
                {
                    Id = 1,
                    Barcode = "123456789",
                    Description = "Office Supplies - Box of Pens",
                    SupplierName = "Office Depot",
                    OrderDate = baseDate,
                    Status = "Pending"
                },
                new Order
                {
                    Id = 2,
                    Barcode = "987654321",
                    Description = "Computer Equipment - Laptop",
                    SupplierName = "Tech Solutions Inc",
                    OrderDate = baseDate.AddDays(2),
                    Status = "Pending"
                },
                new Order
                {
                    Id = 3,
                    Barcode = "555666777",
                    Description = "Furniture - Office Chairs (Set of 5)",
                    SupplierName = "Furniture Plus",
                    OrderDate = baseDate.AddDays(-2),
                    ArrivalDate = baseDate.AddDays(3),
                    Status = "Arrived"
                }
            );
        }
    }
}
