using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using IncomingGoodsBackend.Data;
using IncomingGoodsBackend.Models;
using IncomingGoodsBackend.DTOs;

namespace IncomingGoodsBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class OrdersController : ControllerBase
    {
        private readonly AppDbContext _context;
        private readonly IWebHostEnvironment _environment;
        private readonly ILogger<OrdersController> _logger;

        public OrdersController(AppDbContext context, IWebHostEnvironment environment, ILogger<OrdersController> logger)
        {
            _context = context;
            _environment = environment;
            _logger = logger;
        }

        // GET: api/orders/barcode/{barcode}
        [HttpGet("barcode/{barcode}")]
        public async Task<ActionResult<OrderDto>> GetOrderByBarcode(string barcode)
        {
            var order = await _context.Orders
                .Include(o => o.Photos)
                .FirstOrDefaultAsync(o => o.Barcode == barcode);

            if (order == null)
            {
                return NotFound(new { message = $"Order with barcode {barcode} not found" });
            }

            var orderDto = MapToDto(order);
            return Ok(orderDto);
        }

        // GET: api/orders
        [HttpGet]
        public async Task<ActionResult<IEnumerable<OrderDto>>> GetAllOrders()
        {
            var orders = await _context.Orders
                .Include(o => o.Photos)
                .ToListAsync();

            var orderDtos = orders.Select(MapToDto).ToList();
            return Ok(orderDtos);
        }

        // GET: api/orders/{id}
        [HttpGet("{id}")]
        public async Task<ActionResult<OrderDto>> GetOrder(int id)
        {
            var order = await _context.Orders
                .Include(o => o.Photos)
                .FirstOrDefaultAsync(o => o.Id == id);

            if (order == null)
            {
                return NotFound();
            }

            return Ok(MapToDto(order));
        }

        // POST: api/orders/{id}/arrival
        [HttpPost("{id}/arrival")]
        public async Task<ActionResult> ReportArrival(int id)
        {
            var order = await _context.Orders.FindAsync(id);

            if (order == null)
            {
                return NotFound(new { message = "Order not found" });
            }

            if (order.IsClosed)
            {
                return BadRequest(new { message = "Cannot modify a closed order" });
            }

            order.ArrivalDate = DateTime.Now;
            order.Status = "Arrived";

            await _context.SaveChangesAsync();

            _logger.LogInformation($"Order {id} arrival reported at {order.ArrivalDate}");

            return Ok(new { message = "Arrival reported successfully", arrivalDate = order.ArrivalDate });
        }

        // POST: api/orders/{id}/completion
        [HttpPost("{id}/completion")]
        public async Task<ActionResult> ReportCompletion(int id)
        {
            var order = await _context.Orders.FindAsync(id);

            if (order == null)
            {
                return NotFound(new { message = "Order not found" });
            }

            if (order.IsClosed)
            {
                return BadRequest(new { message = "Cannot modify a closed order" });
            }

            if (order.ArrivalDate == null)
            {
                return BadRequest(new { message = "Cannot complete an order that hasn't arrived yet" });
            }

            order.CompletionDate = DateTime.Now;
            order.Status = "Completed";

            await _context.SaveChangesAsync();

            _logger.LogInformation($"Order {id} completion reported at {order.CompletionDate}");

            return Ok(new { message = "Completion reported successfully", completionDate = order.CompletionDate });
        }

        // POST: api/orders/{id}/close
        [HttpPost("{id}/close")]
        public async Task<ActionResult> CloseOrder(int id)
        {
            var order = await _context.Orders.FindAsync(id);

            if (order == null)
            {
                return NotFound(new { message = "Order not found" });
            }

            if (order.IsClosed)
            {
                return BadRequest(new { message = "Order is already closed" });
            }

            order.IsClosed = true;
            order.Status = "Closed";

            await _context.SaveChangesAsync();

            _logger.LogInformation($"Order {id} closed");

            return Ok(new { message = "Order closed successfully" });
        }

        // POST: api/orders/{id}/photos
        [HttpPost("{id}/photos")]
        public async Task<ActionResult> UploadPhoto(int id, [FromBody] PhotoUploadDto photoDto)
        {
            var order = await _context.Orders.FindAsync(id);

            if (order == null)
            {
                return NotFound(new { message = "Order not found" });
            }

            if (order.IsClosed)
            {
                return BadRequest(new { message = "Cannot add photos to a closed order" });
            }

            try
            {
                // Validate file extension
                var fileExtension = Path.GetExtension(photoDto.FileName)?.ToLowerInvariant();
                var allowedExtensions = new[] { ".jpg", ".jpeg", ".png", ".gif", ".bmp" };
                
                if (string.IsNullOrEmpty(fileExtension) || !allowedExtensions.Contains(fileExtension))
                {
                    return BadRequest(new { message = "Invalid file type. Only jpg, jpeg, png, gif, and bmp files are allowed." });
                }

                // Validate base64 data size (limit to 10MB)
                const int maxSizeInBytes = 10 * 1024 * 1024; // 10MB
                if (photoDto.Base64Data.Length > maxSizeInBytes * 4 / 3) // Base64 is ~33% larger
                {
                    return BadRequest(new { message = "File size exceeds the maximum allowed size of 10MB." });
                }

                // Validate base64 format
                if (!IsValidBase64String(photoDto.Base64Data))
                {
                    return BadRequest(new { message = "Invalid base64 data format." });
                }

                // Create uploads directory if it doesn't exist
                var uploadsDir = Path.Combine(_environment.ContentRootPath, "uploads");
                if (!Directory.Exists(uploadsDir))
                {
                    Directory.CreateDirectory(uploadsDir);
                }

                // Generate unique filename with validated extension
                var uniqueFileName = $"{id}_{Guid.NewGuid()}{fileExtension}";
                var filePath = Path.Combine(uploadsDir, uniqueFileName);

                // Decode and save the base64 image
                var imageBytes = Convert.FromBase64String(photoDto.Base64Data);
                await System.IO.File.WriteAllBytesAsync(filePath, imageBytes);

                // Create photo record
                var photo = new OrderPhoto
                {
                    OrderId = id,
                    FileName = photoDto.FileName,
                    FilePath = filePath,
                    UploadDate = DateTime.Now
                };

                _context.OrderPhotos.Add(photo);
                await _context.SaveChangesAsync();

                _logger.LogInformation($"Photo uploaded for order {id}: {uniqueFileName}");

                return Ok(new 
                { 
                    message = "Photo uploaded successfully", 
                    photoId = photo.Id,
                    fileName = photo.FileName,
                    uploadDate = photo.UploadDate
                });
            }
            catch (FormatException)
            {
                return BadRequest(new { message = "Invalid base64 data format." });
            }
            catch (Exception ex)
            {
                _logger.LogError($"Error uploading photo: {ex.Message}");
                return StatusCode(500, new { message = "Error uploading photo", error = ex.Message });
            }
        }

        // Helper method to validate base64 string
        private bool IsValidBase64String(string base64String)
        {
            if (string.IsNullOrEmpty(base64String))
                return false;

            // Remove any whitespace
            base64String = base64String.Trim();

            // Check if the length is valid for base64
            if (base64String.Length % 4 != 0)
                return false;

            try
            {
                Convert.FromBase64String(base64String);
                return true;
            }
            catch
            {
                return false;
            }
        }

        // Helper method to map Order to OrderDto
        private OrderDto MapToDto(Order order)
        {
            return new OrderDto
            {
                Id = order.Id,
                Barcode = order.Barcode,
                Description = order.Description,
                SupplierName = order.SupplierName,
                OrderDate = order.OrderDate,
                ArrivalDate = order.ArrivalDate,
                CompletionDate = order.CompletionDate,
                IsClosed = order.IsClosed,
                Status = order.Status,
                Photos = order.Photos?.Select(p => new PhotoDto
                {
                    Id = p.Id,
                    FileName = p.FileName,
                    UploadDate = p.UploadDate
                }).ToList() ?? new List<PhotoDto>()
            };
        }
    }
}
