// Author: Lu Yixiang
// Created: 4 Oct 2006
// Last Modified: 9 Oct 2006
//modified  10.08.2017 Dominik Madea
//https://www.codeproject.com/Articles/15884/Compressible-Bitmap-Using-In-Memory-Image-Compress

using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.IO;
using System.Drawing.Imaging;

namespace Lyx
{
    /// <summary>
    /// A wrapper of image that serves as a compromised solution in terms of memory usage and loading time.
    /// </summary>
    /// <remarks>
    /// Scenario 1: Loading a image from disk every time it's used waste a lot of time on loading. 
    /// Scenario 2: Storing all the image required within memory take a lot of memory. 
    /// This class provide a intermediate solution as it take less loading time than the senario 1 and 
    /// takes up less memory space than scenario 2.
    /// </remarks>
	[Serializable]
    public class CompressibleImage
    {
        private Image decompressedImg = null;
        private MemoryStream stream = null;
		//private ImageFormat format = ImageFormat.Png;

		/// <summary>
		/// Creates an <see cref="CompressibleImage"/> instance, given the original image and format for compression.
		/// </summary>
		/// <param name="original">orginal image</param>
		/// the format used for in-memory image compression. Note that compression can cause quality loss
		/// if the format specified is not lossless, such as Jpeg.
		/// </param>
		public CompressibleImage(Image original)
		{
			this.decompressedImg = original;
		}

		///// <summary>
		///// Creates an<see cref= "CompressibleImage" /> instance, given the original image and format for compression.
		///// </summary>
		///// <param name = "original" > orginal image</param>
		///// <param name = "formatForCompression" >
		///// the format used for in-memory image compression.Note that compression can cause quality loss
		///// if the format specified is not lossless, such as Jpeg.
		///// </param>
		////public CompressibleImage(Image original, ImageFormat formatForCompression)
		////{
		////	this.decompressedImg = original;
		////	this.format = formatForCompression;
		////}


		/// <summary>
		/// Creates an <see cref="CompressibleImage"/> instance, given the stream containing the compressed image.
		/// </summary>
		/// <param name="stream">stream containing the compressed image</param>
		public CompressibleImage(MemoryStream stream)
        {
            this.stream = stream;
        }

        /// <summary>
        /// Gets a value indicating if the image is compressed
        /// </summary>
        public bool IsCompressed
        {
            get
            {
                return (decompressedImg == null);
            }
        }

        /// <summary>
        /// Gets the uncompressed image. If the image is compressed, it will be first uncompressed.
        /// </summary>
        public Image GetDecompressedImage()
        {
            if (decompressedImg == null)
            {
                stream.Seek(0, SeekOrigin.Begin);
                decompressedImg = new Bitmap(stream);
            }
            return decompressedImg;
        }

        /// <summary>
        /// Clears the uncompressed image, leaving the compressed one in memory.
        /// </summary>
        public void CompressImage()
        {
            if (decompressedImg != null)
            {
                if (stream == null)
                {
                    stream = new MemoryStream();
                    decompressedImg.Save(stream, ImageFormat.Png);
                }
				decompressedImg.Dispose();
				decompressedImg = null;
            }
        }

    }
}
